import {
  bindChangeEvents,
  bindFormSubmit,
  createPaginationController,
  createQueryString,
  getInputValue,
  request,
} from "./modules/appShared.js";

// 餐廳列表表格內容容器（tbody）。
const tableBody = document.getElementById("tableBody");

// 分頁控制器：統一處理上一頁、下一頁與頁碼顯示。
const pagination = createPaginationController({
  prevButtonId: "prevPage",
  nextButtonId: "nextPage",
  pageInfoSelector: ".switchPageBtn div",
  onPageChange: async () => {
    await listRestaurant();
  },
});

document.addEventListener("DOMContentLoaded", async () => {
  // 攔截查詢表單送出，改為 AJAX 查詢。
  bindFormSubmit("listForm", async () => {
    pagination.reset();
    await listRestaurant();
  });

  // 切換分類/排序條件時，自動回第一頁重新查詢。
  bindChangeEvents(["category", "orderBy", "sort"], async () => {
    pagination.reset();
    await listRestaurant();
  });

  await listRestaurant();
});

// 事件委派：處理列表中的刪除按鈕。
tableBody?.addEventListener("click", deleteRestaurant);

/** 根據目前篩選條件查詢餐廳列表。 */
async function listRestaurant() {
  const query = createQueryString({
    category: getInputValue("category"),
    search: getInputValue("search"),
    orderBy: getInputValue("orderBy"),
    sort: getInputValue("sort"),
    offset: pagination.getOffset(),
  });

  // 呼叫後端餐廳 API 取得分頁資料。
  const response = await request(`/restaurants?${query}`);
  if (!response) {
    return;
  }

  if (!response.ok) {
    window.showAppModal(`查詢餐廳失敗（${response.status}）`);
    return;
  }

  // 同步分頁狀態並渲染表格列。
  const result = await response.json();
  pagination.sync(result);
  renderRestaurantRows(result.results ?? []);
}

/** 將餐廳資料渲染進列表表格。 */
function renderRestaurantRows(restaurants) {
  if (!tableBody) {
    return;
  }

  tableBody.innerHTML = "";
  restaurants.forEach((restaurant) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${restaurant.groupDisplayOrder ?? "-"}</td>
      <td class="noteCell">${restaurant.restaurantName}</td>
      <td>
        <img src="${restaurant.imageUrl}"
             alt="餐廳圖片"
             width="100"
             onerror="this.onerror=null;this.src='/images/defaultRestaurant.jpg';"/>
      </td>
      <td>${restaurant.category}</td>
      <td>${restaurant.visitedCount}</td>
      <td>${restaurant.lastSelectedAt ?? "-"}</td>
      <td>${restaurant.updatedAt}</td>
      <td class="noteCell">${restaurant.note ?? ""}</td>
      <td>
        <div class="buttonGroup">
          <button class="btn btn-yellow" data-route="/dinnerHome/restaurants/${restaurant.restaurantId}/dishes">菜單</button>
          <button class="btn btn-default" data-route="/dinnerHome/restaurants/${restaurant.restaurantId}/edit">修改</button>
          <button class="delete-btn btn btn-default" data-id="${restaurant.restaurantId}">刪除</button>
        </div>
      </td>
    `;
    tableBody.appendChild(tr);
  });
}

async function deleteRestaurant(event) {
  const deleteButton = event.target.closest(".delete-btn");
  if (!deleteButton || !tableBody?.contains(deleteButton)) {
    return;
  }

  event.preventDefault();
  // 刪除前先跳確認視窗。
  const id = deleteButton.getAttribute("data-id");
  const shouldDelete = await window.showAppConfirm("確定要刪除這間餐廳嗎？");
  if (!shouldDelete) {
    return;
  }

  // 送出刪除請求並依結果提示。
  const response = await request(`/restaurants/${id}`, { method: "DELETE" });
  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("刪除成功！");
    await listRestaurant();
  } else if (response.status === 403) {
    window.showAppModal("只有管理員帳號可以刪除餐廳資料！");
  } else {
    window.showAppModal(`刪除失敗（${response.status}）`);
  }
}