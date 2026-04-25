let currentRestaurantId;

const randomButton = document.getElementById("random-btn");
randomButton.addEventListener("click", randomRestaurant);

/** 點擊「抽！」時執行的邏輯 */
async function randomRestaurant() {
    const category = document.getElementById("categoryLabel").value; // 抽餐廳時選擇的類別
    const params = new URLSearchParams(); // 建立查詢參數物件
    if (category){
        params.append("category", category); // 若有選類別，加入參數
    }

    const url = `/random?${params.toString()}`; // 組成 API 請求網址

    // 向後端發送 GET 請求，取得隨機餐廳資訊
    const response = await fetch(url).catch((error) => {
        console.error("抽餐廳時發生錯誤:", error);
        window.showAppModal("系統發生錯誤（網路或連線異常）！");
        return null;
    });

    if (!response) {
        return;
    }

    if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
            window.showAppModal("請先登入後再使用抽餐廳功能。");
        } else {
            window.showAppModal(`取得餐廳失敗（${response.status}）`);
        }
        return;
    }

    const data = await response.json(); // 將回應轉為 JSON 物件
    currentRestaurantId = data.restaurantId; // 儲存目前抽到的餐廳 ID

    // 將資料顯示在畫面上（餐廳圖片）
    const imageUrl = document.getElementById('imageUrl');
    imageUrl.src = data.imageUrl || '/images/defaultRestaurant.jpg'; // 若沒圖片則顯示預設圖

    // 餐廳名稱
    const restaurantName =document.getElementById('restaurantName');
    restaurantName.innerText = data.restaurantName;

    // 類別
    const categoryText = document.getElementById('category');
    categoryText.innerText = data.category;

    // 被選過幾次
    const visitedCount = document.getElementById('visitedCount');
    visitedCount.innerText = data.visitedCount;

    // 上次被選的時間
    const lastSelectedAt = document.getElementById('lastSelectedAt');
    lastSelectedAt.innerText = data.lastSelectedAt;

    // 最後更新資料時間
    const updatedAt = document.getElementById('updatedAt');
    updatedAt.innerText = data.updatedAt;

    // 備註
    const note = document.getElementById('note');
    note.innerText = data.note;
}

const resetButton = document.getElementById("categoryLabel");
resetButton.addEventListener("change", resetRandom);

/** 當使用者改變類別下拉選單時，重置抽選紀錄 */
async function resetRandom() {
    const response = await fetch('/clearRandom', {
        method: 'POST' // 通知後端清除目前的抽選紀錄
    }).catch((error) => {
        console.error("重置抽籤時發生錯誤:", error);
        window.showAppModal("系統發生錯誤（網路或連線異常）！");
        return null;
    });

    if (!response) {
        return;
    }

    if (response.ok) {
        window.showAppModal("抽籤紀錄已清除，開始新的抽選！");
    } else if (response.status === 401 || response.status === 403) {
        window.showAppModal("請先登入後再重置抽籤紀錄。");
    } else {
        window.showAppModal(`重抽失敗（${response.status}）`);
    }
}

/**
 * 將目前抽到的餐廳 ID 封裝成函式 export 出去
 * - 其他 JS 模組（例如「選擇這間」）可以呼叫這個函式取得目前的餐廳 ID
 */
export function getCurrentId(){
    return currentRestaurantId;
}