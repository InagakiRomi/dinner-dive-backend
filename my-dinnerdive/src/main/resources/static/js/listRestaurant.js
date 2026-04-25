// 定義全域變數：分頁用
let offset; // 目前的資料起始位置（例如第幾筆開始）
let limit;  // 每頁最多顯示幾筆（由後端回傳）
let total;  // 總資料筆數（由後端回傳）

document.addEventListener("DOMContentLoaded", function (){
    listRestaurant(); // 初次載入時就列出餐廳

    // 阻止表單預設提交行為，改為 AJAX 載入
    const preventForm = document.getElementById("listForm");
    preventForm.addEventListener("submit", preventFormSubmit);

    function preventFormSubmit(event) {
        event.preventDefault(); // 不重新整理頁面
        offset = 0;             // 重設分頁為第一頁
        listRestaurant();       // 重新載入餐廳資料
    }

    // 如果使用者選擇分類、排序欄位或排序方式時，就重新查詢
    const categorySelect = document.getElementById("category");
    categorySelect.addEventListener("change", function () {
        offset = 0;
        listRestaurant();
    });

    const orderBySelect = document.getElementById("orderBy");
    orderBySelect.addEventListener("change", function () {
        offset = 0;
        listRestaurant();
    });

    const sortSelect = document.getElementById("sort");
    sortSelect.addEventListener("change", function () {
        offset = 0;
        listRestaurant();
    });
})

// 上一頁按鈕的事件處理
const prevButton = document.getElementById("prevPage")
prevButton.addEventListener("click", function () {
    if(offset >= limit){
        offset = offset - limit;
        listRestaurant();
    }
});

// 下一頁按鈕的事件處理
const nextButton = document.getElementById("nextPage")
nextButton.addEventListener("click", function () {
    if(offset < total-limit){
        offset = offset + limit;
        listRestaurant(); // 重新載入下一頁資料
    }
});

/** 根據查詢條件列出餐廳資料 */
async function listRestaurant(){

    // 從下拉選單和輸入框取得查詢條件
    const category = document.getElementById("category").value;
    const search = document.getElementById("search").value;
    const orderBy = document.getElementById("orderBy").value;
    const sort = document.getElementById("sort").value;

    // 組合成查詢字串
    const params = new URLSearchParams();
    if (category){
        params.append("category", category);
    } 
    if (search){
        params.append("search", search);
    }
    if (orderBy){
        params.append("orderBy", orderBy);
    }
    if (sort){
        params.append("sort", sort);
    }

    // 設定分頁參數
    params.append("offset", offset || 0);
    const url = `/restaurants?${params.toString()}`;

    // 發送 GET 請求向後端查資料
    const response = await fetch(url).catch((error) => {
        console.error("查詢餐廳時發生錯誤:", error);
        window.showAppModal("系統發生錯誤（網路或連線異常）！");
        return null;
    });

    if (!response) {
        return;
    }

    if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
            window.showAppModal("請先登入後再查看餐廳資料。");
        } else {
            window.showAppModal(`查詢餐廳失敗（${response.status}）`);
        }
        return;
    }

    const result = await response.json();

    // 從回應中取得分頁資訊
    offset = result.offset
    limit = result.limit;
    total = result.total;
    const data = result.results;

    // 清空表格內容，重新塞資料
    const tbody = document.getElementById('tableBody');
    tbody.innerHTML = '';

    // 將每筆餐廳資料插入表格中
    data.forEach(restaurant => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${restaurant.restaurantId}</td>
            <td class="noteCell">${restaurant.restaurantName}</td>
            <td>
                <img src="${restaurant.imageUrl}"
                     alt="餐廳圖片"
                     width="100"
                     onerror="this.onerror=null;this.src='/images/defaultRestaurant.jpg';"/>
            </td>
            <td>${restaurant.category}</td>
            <td>${restaurant.visitedCount}</td>
            <td>${restaurant.lastSelectedAt ?? '-'}</td>
            <td>${restaurant.updatedAt}</td>
            <td class="noteCell">${restaurant.note}</td>
            <td>
                <div class="buttonGroup">
                    <button class="btn btn-yellow" onclick="location.href='/dinnerHome/restaurants/${restaurant.restaurantId}/dishes'">菜單</button>
                    <button class="btn btn-default" onclick="location.href='/dinnerHome/restaurants/${restaurant.restaurantId}/edit'">修改</button>
                    <button class="delete-btn btn btn-default" data-id="${restaurant.restaurantId}">刪除</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // 顯示目前頁數與總頁數
    const pageInfo = document.querySelector(".switchPageBtn div");
    const currentPage = Math.floor(offset / limit) + 1;
    const totalPages = Math.ceil(total / limit);
    pageInfo.textContent = `第${currentPage}頁 / 共${totalPages}頁`;

    // 根據目前頁面來決定按鈕是否禁用
    prevButton.disabled = currentPage === 1;
    nextButton.disabled = currentPage === totalPages;
};

// 表格內綁定刪除按鈕事件（事件委派）
const tableBody = document.getElementById('tableBody')
tableBody.addEventListener('click', deleteRestaurant);

/** 根刪除餐廳資料的處理邏輯 */
async function deleteRestaurant(event) {
    const deleteButton = event.target.closest(".delete-btn");

    if (deleteButton && tableBody.contains(deleteButton)) {
        event.preventDefault();
        const id = deleteButton.getAttribute("data-id");

        // 發送 DELETE 請求刪除資料
        const response = await fetch(`/restaurants/${id}`, {
            method: "DELETE"
        }).catch((error) => {
            console.error("刪除餐廳時發生錯誤:", error);
            window.showAppModal("系統發生錯誤（網路或連線異常）！");
            return null;
        });

        if (!response) {
            return;
        }

        if (response.ok) {
            window.showAppModal("刪除成功！");
            await listRestaurant(); // 刪除成功後重新載入列表
        } else if (response.status === 401 || response.status === 403) {
            window.showAppModal("只有管理員帳號可以刪除餐廳資料！");
        } else {
            window.showAppModal(`刪除失敗（${response.status}）`);
        }
    }
};