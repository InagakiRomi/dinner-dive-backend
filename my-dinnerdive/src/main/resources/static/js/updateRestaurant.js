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
function updateRestaurant(){

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
    fetch(`/restaurants/${getAll.getRestaurantId()}`, {
        method: 'PUT',
        headers: getAll.getHeaders(),         // 設定標頭，例如 Content-Type: application/json
        body: JSON.stringify(restaurantJson)  // 將資料物件轉成 JSON 字串
    })
    .then(response => {
        if (response.ok) {
            // 修改成功後導回餐廳清單頁
            window.location.href = "/dinnerHome/listRestaurant";
            alert("修改成功！");
        } else {
            alert("修改失敗！請確認資料是否正確");
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("發生錯誤，請稍後再試！");
    });
}