import getAll from './modules/dishDataBuilder.js';

document.addEventListener("DOMContentLoaded", function (){
    const preventForm = document.getElementById("createForm");
    // 當使用者點擊按鈕時，執行 preventFormSubmit 函式
    preventForm.addEventListener("submit", preventFormSubmit);

    // 攔截表單提交事件，避免頁面重新整理
    function preventFormSubmit(event) {
        // 阻止表單的預設提交動作
        event.preventDefault();
        // 改為手動觸發送出資料的函式
        createDish();
    }
})

/** 導向餐點列表頁面 */
function redirectToDishesPage(restaurantId) {
    window.location.href = `/dinnerHome/restaurants/${restaurantId}/dishes`;
}

/** 使用者「新增餐廳」後執行的邏輯 */
function createDish(){
    // 整理表單輸入資料，合併成一個 JSON 格式的物件
    var dishJson = {
        ...getAll.getRestaurantId(),
        ...getAll.getPrice(),
        ...getAll.getDishName()
    }

    // 使用 fetch 向後端發送 POST 請求，新增一筆餐點資料
    fetch('/dishes', {
        method: "POST",
        headers: getAll.getHeaders(),          // 設定標頭，例如 Content-Type: application/json
        body: JSON.stringify(dishJson)  // 將 JavaScript 物件轉為 JSON 字串送出
    })
    .then((response) => {
        if (response.ok) {
            alert("餐點新增成功！");
            redirectToDishesPage(dishJson.restaurantId);
        } else {
            alert("新增失敗，請再試一次");
        }
    })
    .catch((error) => {
        alert("系統發生錯誤！");
    })
}

/** 從新增餐點回到上一個頁面 */
document.getElementById('previousBtn').addEventListener("click", previousPage);
async function previousPage() {
    // 導向新增餐點頁面
    const { restaurantId } = getRestaurantId();
    redirectToDishesPage(restaurantId);
}