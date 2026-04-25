import getAll from './modules/restaurantDataBuilder.js';

document.addEventListener("DOMContentLoaded", function (){
    //同步餐廳分類資料庫
    const categorySelect = document.getElementById("category");
    const selectedValue = categorySelect.getAttribute("value");
    if (selectedValue) {
        categorySelect.value = selectedValue;
    }

    const preventForm = document.getElementById("updateForm");
    preventForm.addEventListener("submit", preventFormSubmit);

    function preventFormSubmit(event) {
        event.preventDefault(); // 防止瀏覽器預設的送出行為（會刷新頁面）
        updateRestaurant();     // 執行自定義的更新函式
    }
})

/** 執行修改餐廳資料邏輯 */
async function updateRestaurant(){

    // 整理所有欄位資料合併成一個 JSON 物件
    var restaurantJson = {
        ...getAll.getRestaurantName(),
        ...getAll.getCategory(),
        ...getAll.getVisitedCount(),
        ...getAll.getLastSelectedAt(),
        ...getAll.getNote(),
        ...getAll.getImageUrl()
    }

    // 呼叫後端 API，更新指定 ID 的餐廳資料
    const response = await fetch(`/restaurants/${getAll.getRestaurantId()}`, {
        method: 'PUT',
        headers: getAll.getHeaders(),         // 設定標頭，例如 Content-Type: application/json
        body: JSON.stringify(restaurantJson)  // 將資料物件轉成 JSON 字串
    }).catch((error) => {
        console.error("修改餐廳時發生錯誤:", error);
        window.showAppModal("系統發生錯誤（網路或連線異常）！");
        return null;
    });

    if (!response) {
        return;
    }

    if (response.ok) {
        window.showAppModal("修改成功！", () => {
            window.location.href = "/dinnerHome/listRestaurant";
        });
    } else if (response.status === 401 || response.status === 403) {
        window.showAppModal("請先登入後再修改餐廳資料。");
    } else {
        window.showAppModal(`修改失敗（${response.status}）`);
    }
}