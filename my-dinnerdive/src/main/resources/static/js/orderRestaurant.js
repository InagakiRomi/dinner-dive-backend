let restaurantId;
const container = document.getElementById('dishContainer');

document.addEventListener("DOMContentLoaded", function () {
    restaurantId = document.body.getAttribute('dataRestaurantId');

    loadRestaurantInfo(); // 顯示餐廳名稱
    listDishes();         // 初始載入菜單資料
});

/** 取得餐廳資訊並更新標題 */
async function loadRestaurantInfo() {
    fetch(`/restaurants/${restaurantId}`)
        .then(response => response.json())
        .then(restaurant => {
            const title = document.getElementById('restaurantTitle');
            title.textContent = `${restaurant.restaurantName}`;
        })
        .catch(error => {
            console.error('取得餐廳資訊時出錯:', error);
        });
}

/** 取得餐點資料 */
async function listDishes() {
fetch(`/restaurants/${restaurantId}/dishes`)
    .then(response => response.json())
    .then(dishes => {

        // 清空容器避免重複顯示
        container.innerHTML = '';

        // 如果資料是空的，就顯示提示文字
        if (dishes.length === 0) {
            const emptyMessage = document.createElement('div');
            emptyMessage.textContent = '請新增餐點';
            emptyMessage.className = 'noDishMessage';
            container.appendChild(emptyMessage);
            return;
        }

        dishes.forEach(dish => {
            // 建立一個新的 div 元素來顯示菜色
            const dishDiv = document.createElement('div');
            dishDiv.className = 'dish';

            dishDiv.innerHTML = `
                <div class="dishRow">
                    <div class="dishName">${dish.dishName}</div>
                    <div class="dishPrice">$${dish.price}</div>
                    <!-- <button class="updateBtn">修改</button> -->
                    <button class="deleteBtn delete-btn" data-id="${dish.dishId}">刪除</button>
                </div>
            `;

            // 把 div 加到網頁上的 container 中
            container.appendChild(dishDiv);
        });
    })
    .catch(error => {
        console.error('取得菜單時出錯:', error);
    });
}

/** 刪除餐點資料的處理邏輯 */
container.addEventListener("click", deleteDish);
async function deleteDish(event) {
    const target = event.target;
    const deleteButton = target.classList.contains('delete-btn');
    if (deleteButton) {
        // 取得要刪除的餐點 ID
        const id = target.getAttribute('data-id');

        // 發送 DELETE 請求刪除資料
        fetch(`/dishes/${id}`, {
            method: "DELETE"
        })
        .then(response => {
            if (response.ok) {
                alert("刪除成功！");
                listDishes(); // 刪除成功後重新載入列表
            } else {
                alert("只有管理員帳號可以刪除餐廳資料！");
            }
        })
        .catch(error => {
            alert("只有管理員帳號可以刪除餐廳資料！");
        });
    }
}

/** 導向新增餐點頁面 */
document.getElementById('addDishBtn').addEventListener("click", addDishPage);
async function addDishPage() {
    // 導向新增餐點頁面
    window.location.href = `/dinnerHome/restaurants/${restaurantId}/dishes/createDish`;
}