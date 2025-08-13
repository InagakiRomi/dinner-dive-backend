/** 取得餐廳 ID */
function getRestaurantId(){
    const restaurantId = document.getElementById('restaurantId').value;
    //var idVar = {"restaurantId": restaurantId};
    return restaurantId;
}

/** 取得餐廳名稱 */
function getRestaurantName(){
    const restaurantName =document.getElementById('restaurantName').value;
    var nameVar = {"restaurantName": restaurantName};
    return nameVar;
}

/** 取得餐廳分類 */
function getCategory(){
    const category = document.getElementById('category').value;
    var categoryVar = {"category": category};
    return categoryVar;
}

/** 取得餐廳被抽選擇的次數 */
function getVisitedCount() {
    const visitedCount = parseInt(document.getElementById('visitedCount').value);
    var visitedCountVar = {"visitedCount": visitedCount};
    return visitedCountVar;
}

/** 取得餐廳最後被選擇的時間 */
function getLastSelectedAt() {
    const lastSelectedAt = document.getElementById('lastSelectedAt').value;
    var lastSelectedAtVar = {"lastSelectedAt": lastSelectedAt};
    return lastSelectedAtVar;
}

/** 取得餐廳的備註 */
function getNote() {
    const note = document.getElementById('note').value;
    var noteVar = {"note": note};
    return noteVar;
}

/** 取得餐廳的圖片 */
function getImageUrl() {
    const imageUrl = document.getElementById('imageUrl').value;
    var imageUrlVar = {"imageUrl": imageUrl};
    return imageUrlVar;
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
    getRestaurantId,
    getRestaurantName,
    getCategory,
    getVisitedCount,
    getLastSelectedAt,
    getNote,
    getImageUrl,
    getHeaders
}

export default getAll;