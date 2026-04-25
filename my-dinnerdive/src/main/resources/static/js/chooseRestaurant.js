import {
    getCurrentId
} from './randomRestaurant.js';

const chooseutton = document.getElementById("choose-btn");
// 當使用者點擊按鈕時，執行 chooseRestaurant 函式
chooseutton.addEventListener("click", chooseRestaurant);

/** 使用者點選「我就吃這間」後執行的邏輯 */
async function chooseRestaurant(){
    // 取得目前抽中的餐廳 ID
    const id = getCurrentId();

    // 發送 PATCH 請求到後端，通知選擇這家餐廳
    const response = await fetch(`/choose/${id}`, {
        method: 'PATCH'
    }).catch((error) => {
        console.error("選擇餐廳時發生錯誤:", error);
        alert("系統發生錯誤（網路或連線異常）！");
        return null;
    });

    if (!response) {
        return;
    }

    if (response.ok) {
        window.location.href = "/dinnerHome/randomRestaurant";
        alert("選擇成功！");
    } else if (response.status === 401 || response.status === 403) {
        alert("請先登入後再選擇餐廳。");
    } else {
        alert(`選擇失敗（${response.status}）`);
    }
}