import {
  bindFormSubmit,
  getInputValue,
  redirectTo,
  request,
} from "./modules/appShared.js";

document.addEventListener("DOMContentLoaded", () => {
  // 攔截表單 submit，避免頁面刷新並改用 AJAX 新增資料。
  bindFormSubmit("createForm", createRestaurant);
});

/** 送出新增餐廳資料。 */
async function createRestaurant() {
  // 將表單欄位整理成後端 API 需要的 JSON 格式。
  const restaurantJson = {
    restaurantName: getInputValue("restaurantName"),
    category: getInputValue("category"),
    note: getInputValue("note"),
    imageUrl: getInputValue("imageUrl"),
  };

  // 呼叫新增餐廳 API。
  const response = await request("/restaurants", {
    method: "POST",
    jsonBody: restaurantJson,
  });
  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("餐廳新增成功！", () => {
      redirectTo("/dinnerHome/listRestaurant");
    });
  } else {
    window.showAppModal(`新增失敗（${response.status}）`);
  }
}