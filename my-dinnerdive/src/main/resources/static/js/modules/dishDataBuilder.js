/** 取得餐點 ID */
function getDishId(){
    const dishId = document.getElementById('dishId').value;
    return dishId;
}

/** 取得餐點對應餐廳編號 */
function getRestaurantId(){
    const restaurantId = document.getElementById('restaurantId').value;
    var restaurantIdVar = {"restaurantId": restaurantId};
    return restaurantIdVar;
}

/** 取得餐點對應餐點價格 */
function getPrice(){
    const price = document.getElementById('price').value;
    var priceVar = {"price": price};
    return priceVar;
}

/** 取得餐點名稱 */
function getDishName() {
    const dishName = document.getElementById('dishName').value;
    var dishNameVar = {"dishName": dishName};
    return dishNameVar;
}

/**
 * 回傳標準 HTTP 請求標頭（headers）
 * - 告訴後端我傳的是 JSON（Content-Type）
 * - 告訴後端我想拿 JSON 回來（Accept）
 */
function getHeaders(){
    // 設定格式
    let headers = {
    "Content-Type": "application/json",   // 告訴後端：這是 JSON 格式
    "Accept": "application/json",         // 告訴後端：我希望回傳也是 JSON
    }

    return headers;
}

// 統一導出所有方法，讓其他 JS 檔可以 import 使用

let getAll = {
    getDishId,
    getRestaurantId,
    getPrice,
    getDishName,
    getHeaders
}

export default getAll;