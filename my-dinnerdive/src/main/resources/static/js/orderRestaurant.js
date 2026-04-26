import { isAuthError, redirectTo, request } from "./modules/appShared.js";

// 目前頁面對應的餐廳編號（由 body data attribute 帶入）。
let restaurantId;
// 菜單渲染容器。
const container = document.getElementById("dishContainer");
// 新增餐點按鈕。
const addDishButton = document.getElementById("addDishBtn");

document.addEventListener("DOMContentLoaded", async () => {
  restaurantId = document.body.getAttribute("dataRestaurantId");

  // 先確認餐廳存在，再載入餐點資料。
  const restaurantExists = await loadRestaurantInfo();
  if (!restaurantExists) {
    showRestaurantNotFound();
    return;
  }

  await listDishes();
});

/** 取得餐廳資訊並更新標題 */
async function loadRestaurantInfo() {
  // 讀取餐廳基本資訊，主要用來顯示標題。
  const response = await request(`/restaurants/${restaurantId}`);

  if (!response) {
    return false;
  }

  if (!response.ok) {
    console.error("取得餐廳資訊失敗，status:", response.status);
    return false;
  }

  const restaurant = await response.json();
  const title = document.getElementById("restaurantTitle");
  title.textContent = `${restaurant.restaurantName}`;
  return true;
}

/** 取得餐點資料 */
async function listDishes() {
  // 讀取指定餐廳的菜單資料。
  const response = await request(`/restaurants/${restaurantId}/dishes`);

  if (!response) {
    return;
  }

  if (!response.ok) {
    console.error("取得菜單失敗，status:", response.status);
    if (response.status === 404) {
      showRestaurantNotFound();
    } else if (isAuthError(response.status)) {
      window.showAppModal("請先登入後再查看餐點資料。");
    }
    return;
  }

  const dishes = await response.json();

  // 清空容器避免重複顯示
  container.innerHTML = "";

  // 如果資料是空的，就顯示提示文字
  if (dishes.length === 0) {
    const emptyMessage = document.createElement("div");
    emptyMessage.textContent = "請新增餐點";
    emptyMessage.className = "noDishMessage";
    container.appendChild(emptyMessage);
    return;
  }

  dishes.forEach((dish) => {
    // 建立一個新的 div 元素來顯示菜色
    const dishDiv = document.createElement("div");
    dishDiv.className = "dish";

    dishDiv.innerHTML = `
                <div class="dishRow">
                    <div class="dishName">${dish.dishName}</div>
                    <div class="dishPrice">$${dish.price}</div>
                    <!-- <button class="updateBtn">修改</button> -->
                    <button type="button" class="deleteBtn delete-btn" data-id="${dish.dishId}">刪除</button>
                </div>
            `;

    // 把 div 加到網頁上的 container 中
    container.appendChild(dishDiv);
  });
}

/** 顯示查無餐廳訊息 */
function showRestaurantNotFound() {
  const title = document.getElementById("restaurantTitle");
  title.textContent = "查無此餐廳";

  container.innerHTML = "";
  const notFoundMessage = document.createElement("div");
  notFoundMessage.textContent = "查無此餐廳";
  notFoundMessage.className = "noDishMessage";
  container.appendChild(notFoundMessage);

  // 查無餐廳時隱藏「新增餐點」按鈕，避免導向無效頁面。
  if (addDishButton) {
    addDishButton.style.display = "none";
  }
}

/** 刪除餐點資料的處理邏輯 */
container.addEventListener("click", deleteDish);
async function deleteDish(event) {
  const deleteButton = event.target.closest(".delete-btn");
  if (deleteButton && container.contains(deleteButton)) {
    event.preventDefault();

    // 取得要刪除的餐點 ID
    // 刪除前先由使用者二次確認。
    const id = deleteButton.getAttribute("data-id");
    const shouldDelete = await window.showAppConfirm("確定要刪除這筆餐點嗎？");
    if (!shouldDelete) {
      return;
    }

    // 發送 DELETE 請求刪除資料
    const response = await request(`/dishes/${id}`, {
      method: "DELETE",
    });

    if (!response) {
      return;
    }

    if (response.ok) {
      window.showAppModal("刪除成功！");
      await listDishes();
    } else if (isAuthError(response.status)) {
      window.showAppModal("只有管理員帳號可以刪除餐廳資料！");
    } else {
      window.showAppModal(`刪除失敗（${response.status}）`);
    }
  }
}

/** 導向新增餐點頁面 */
if (addDishButton) {
  addDishButton.addEventListener("click", addDishPage);
}
function addDishPage() {
  // 導向目前餐廳的新增餐點頁。
  redirectTo(`/dinnerHome/restaurants/${restaurantId}/dishes/createDish`);
}
