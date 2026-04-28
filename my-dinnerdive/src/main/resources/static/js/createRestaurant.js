import {
  bindFormSubmit,
  getInputValue,
  getNumberInputValue,
  request,
} from "./modules/appShared.js";

document.addEventListener("DOMContentLoaded", () => {
  loadNextGroupDisplayOrder();
  // 攔截表單 submit，避免頁面刷新並改用 AJAX 新增資料。
  bindFormSubmit("createForm", createRestaurant);
});

/** 載入新增餐廳時的下一個群組顯示排序值。 */
async function loadNextGroupDisplayOrder() {
  const response = await request("/restaurants/nextGroupDisplayOrder");
  if (!response) {
    return;
  }

  if (!response.ok) {
    window.showAppModal(`取得排序編號失敗（${response.status}）`);
    return;
  }

  const groupDisplayOrder = await response.json();
  const orderInput = document.getElementById("groupDisplayOrder");
  if (orderInput) {
    orderInput.value = groupDisplayOrder;
  }
}

/** 送出新增餐廳資料。 */
async function createRestaurant() {
  // 將表單欄位整理成後端 API 需要的 JSON 格式。
  const restaurantJson = {
    restaurantName: getInputValue("restaurantName"),
    category: getInputValue("category"),
    groupDisplayOrder: getNumberInputValue("groupDisplayOrder", null),
    note: getInputValue("note"),
    imageUrl: getInputValue("imageUrl"),
  };

  // 呼叫新增餐廳 API。
  const response = await request("/restaurants", {
    method: "POST",
    jsonBody: restaurantJson,
  });
  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("餐廳新增成功！");
    clearCreateForm();
    await loadNextGroupDisplayOrder();
  } else {
    window.showAppModal(`新增失敗（${response.status}）`);
  }
}

/** 新增成功後清空可編輯欄位，保留自動排序欄位。 */
function clearCreateForm() {
  const restaurantNameInput = document.getElementById("restaurantName");
  if (restaurantNameInput) {
    restaurantNameInput.value = "";
  }

  const noteInput = document.getElementById("note");
  if (noteInput) {
    noteInput.value = "";
  }

  const imageUrlInput = document.getElementById("imageUrl");
  if (imageUrlInput) {
    imageUrlInput.value = "";
  }
}