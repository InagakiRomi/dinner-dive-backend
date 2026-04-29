import { createQueryString, request } from "./modules/appShared.js";

// 頁面上會重複使用的 DOM 節點與後端注入設定
const tableBody = document.getElementById("memberTableBody");
const groupNameForm = document.getElementById("groupNameForm");
const groupNameInput = document.getElementById("groupNameInput");
const addMemberForm = document.getElementById("addMemberForm");
const targetUserIdInput = document.getElementById("targetUserIdInput");
const pageConfig = window.memberPageConfig ?? {};

// 進入頁面後先綁事件，再載入成員列表
document.addEventListener("DOMContentLoaded", async () => {
  bindEvents();
  await loadMembers();
});

function bindEvents() {
  // 團隊名稱更新
  groupNameForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    await updateGroupName();
  });

  addMemberForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    await addMemberByUserId();
  });

  // 使用事件委派處理表格內動態產生的按鈕
  tableBody?.addEventListener("click", async (event) => {
    const transferButton = event.target.closest(".transfer-admin-btn");
    if (transferButton) {
      await transferAdmin(transferButton.dataset.userId);
      return;
    }

    const deleteButton = event.target.closest(".delete-member-btn");
    if (deleteButton) {
      await deleteMember(
        deleteButton.dataset.userId,
        deleteButton.dataset.username,
      );
      return;
    }

    const leaveButton = event.target.closest(".leave-group-btn");
    if (leaveButton) {
      await leaveGroup();
    }
  });
}

async function loadMembers() {
  // 後端回傳 null/undefined 代表請求層已處理錯誤，這裡直接結束
  const response = await request("/users/group-members");
  if (!response) {
    return;
  }

  if (!response.ok) {
    window.showAppModal(`載入成員失敗（${response.status}）`);
    return;
  }

  const members = await response.json();
  renderRows(Array.isArray(members) ? members : []);
}

function renderRows(members) {
  if (!tableBody) {
    return;
  }
  tableBody.innerHTML = "";

  // 依 userId 升冪固定顯示順序，避免畫面跳動
  const sortedMembers = [...members].sort(
    (a, b) => (a.userId ?? 0) - (b.userId ?? 0),
  );

  sortedMembers.forEach((member) => {
    const isCurrentUser = member.username === pageConfig.username;
    const isAdminMember = member.roles === "ADMIN";
    const actionHtml = pageConfig.isAdmin
      ? isCurrentUser
        ? "-"
        : `<div class="buttonGroup">
             <button class="btn btn-default transfer-admin-btn" data-user-id="${member.userId}" ${
               isAdminMember ? "disabled" : ""
             }>轉移管理員</button>
             <button class="btn btn-default delete-member-btn" data-user-id="${member.userId}" data-username="${member.username}">刪除成員</button>
           </div>`
      : isCurrentUser
        ? `<button class="btn btn-default leave-group-btn">退出群組</button>`
        : "-";
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${member.username}</td>
      <td class="${isAdminMember ? "member-role-admin" : ""}">${isAdminMember ? "管理員" : "一般使用者"}</td>
      <td>${actionHtml}</td>
    `;
    tableBody.appendChild(row);
  });
}

async function updateGroupName() {
  const groupName = groupNameInput?.value?.trim();
  if (!groupName) {
    window.showAppModal("團隊名稱不可為空");
    return;
  }

  const response = await request(
    `/users/group-name?${createQueryString({ groupName })}`,
    {
      method: "PATCH",
    },
  );
  if (!response) {
    return;
  }

  // 成功後刷新頁面，確保所有與團隊名稱相關區塊同步更新
  if (response.ok) {
    window.showAppModal("團隊名稱已更新", () => {
      window.location.reload();
    });
    return;
  }

  window.showAppModal(`更新團隊名稱失敗（${response.status}）`);
}

async function transferAdmin(targetUserId) {
  // 轉移管理員屬於高風險操作，需二次確認
  const confirmed = await window.showAppConfirm("確定要轉移管理員權限嗎？");
  if (!confirmed) {
    return;
  }

  const response = await request(
    `/users/transfer-admin?${createQueryString({ nextAdminUserId: targetUserId })}`,
    { method: "PATCH" },
  );
  if (!response) {
    return;
  }

  // 權限已移轉，導回登出讓使用者以新角色重新進入
  if (response.ok) {
    window.showAppModal("管理員已成功轉移，請重新登入。", () => {
      window.location.href = "/logout";
    });
    return;
  }

  window.showAppModal(`轉移管理員失敗（${response.status}）`);
}

async function deleteMember(targetUserId, username) {
  // 刪除前先顯示成員名稱，降低誤刪機率
  const confirmed = await window.showAppConfirm(
    `確定要將成員 ${username} 移出群組嗎？`,
  );
  if (!confirmed) {
    return;
  }

  const response = await request(
    `/users/group-members?${createQueryString({ targetUserId })}`,
    { method: "DELETE" },
  );
  if (!response) {
    return;
  }

  // 刪除成功後重新拉取列表，避免前端狀態與後端不一致
  if (response.ok) {
    window.showAppModal("成員已移出群組");
    await loadMembers();
    return;
  }

  window.showAppModal(`移出成員失敗（${response.status}）`);
}

async function leaveGroup() {
  const confirmed = await window.showAppConfirm("確定要退出群組嗎？");
  if (!confirmed) {
    return;
  }

  const response = await request("/users/group-members/self", { method: "DELETE" });
  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("你已退出群組", () => {
      window.location.reload();
    });
    return;
  }

  let errorMessage = "";
  try {
    const errorBody = await response.json();
    errorMessage =
      typeof errorBody?.error === "string" ? errorBody.error.trim() : "";
  } catch (error) {
    console.warn("讀取退群錯誤訊息失敗:", error);
  }

  if (errorMessage) {
    window.showAppModal(errorMessage);
    return;
  }

  window.showAppModal(`退出群組失敗（${response.status}）`);
}

async function addMemberByUserId() {
  const userIdValue = targetUserIdInput?.value?.trim();
  const targetUserId = Number(userIdValue);
  if (!Number.isInteger(targetUserId) || targetUserId <= 0) {
    window.showAppModal("請輸入有效的使用者 ID");
    return;
  }

  const response = await request(
    `/users/group-members?${createQueryString({ targetUserId })}`,
    { method: "POST" },
  );
  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("成員已加入");
    if (targetUserIdInput) {
      targetUserIdInput.value = "";
    }
    await loadMembers();
    return;
  }

  let errorMessage = "";
  try {
    const errorBody = await response.json();
    errorMessage =
      typeof errorBody?.error === "string" ? errorBody.error.trim() : "";
  } catch (error) {
    console.warn("讀取新增成員錯誤訊息失敗:", error);
  }

  if (errorMessage) {
    window.showAppModal(errorMessage);
    return;
  }

  window.showAppModal(`新增成員失敗（${response.status}）`);
}
