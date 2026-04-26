let restaurantId;
const container = document.getElementById("dishContainer");
const addDishButton = document.getElementById("addDishBtn");

document.addEventListener("DOMContentLoaded", async function () {
  restaurantId = document.body.getAttribute("dataRestaurantId");

  const restaurantExists = await loadRestaurantInfo(); // 顯示餐廳名稱
  if (!restaurantExists) {
    showRestaurantNotFound();
    return;
  }

  listDishes(); // 初始載入菜單資料
});

/** 取得餐廳資訊並更新標題 */
async function loadRestaurantInfo() {
  const response = await fetch(`/restaurants/${restaurantId}`).catch((error) => {
    console.error("取得餐廳資訊時出錯:", error);
    return null;
  });

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
  const response = await fetch(`/restaurants/${restaurantId}/dishes`).catch(
    (error) => {
      console.error("取得菜單時出錯:", error);
      return null;
    },
  );

  if (!response) {
    return;
  }

  if (!response.ok) {
    console.error("取得菜單失敗，status:", response.status);
    if (response.status === 404) {
      showRestaurantNotFound();
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
    const id = deleteButton.getAttribute("data-id");
    const shouldDelete = await window.showAppConfirm("確定要刪除這筆餐點嗎？");
    if (!shouldDelete) {
      return;
    }

    // 發送 DELETE 請求刪除資料
    const response = await fetch(`/dishes/${id}`, {
      method: "DELETE",
    }).catch((error) => {
      console.error("刪除餐點時發生錯誤:", error);
      window.showAppModal("系統發生錯誤（網路或連線異常）！");
      return null;
    });

    if (!response) {
      return;
    }

    console.log("DELETE /dishes status:", response.status, "ok:", response.ok);

    if (response.ok) {
      window.showAppModal("刪除成功！");
      await listDishes(); // 刪除成功後重新載入列表
    } else if (response.status === 401 || response.status === 403) {
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
async function addDishPage() {
  // 導向新增餐點頁面
  window.location.href = `/dinnerHome/restaurants/${restaurantId}/dishes/createDish`;
}
