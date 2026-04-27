import { getCurrentId } from "./randomRestaurant.js";
import { redirectTo, request } from "./modules/appShared.js";

// 「就決定選這間」按鈕。
const chooseButton = document.getElementById("choose-btn");
// 當使用者點擊按鈕時，執行 chooseRestaurant 函式
chooseButton.addEventListener("click", chooseRestaurant);

/** 使用者點選「我就吃這間」後執行的邏輯 */
async function chooseRestaurant() {
  // 取得目前抽中的餐廳 ID
  const id = getCurrentId();
  if (!id) {
    // 尚未抽籤時，不允許直接送出選擇。
    window.showAppModal("請先開始抽選餐廳。");
    return;
  }

  // 發送 PATCH 請求到後端，通知選擇這家餐廳
  const response = await request(`/choose/${id}`, {
    method: "PATCH",
  });

  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("選擇成功！", () => {
      redirectTo("/dinnerHome/randomRestaurant");
    });
  } else if (response.status === 400) {
    window.showAppModal(`請先開始抽選餐廳。`);
  } else {
    window.showAppModal(`選擇失敗（${response.status}）`);
  }
}
