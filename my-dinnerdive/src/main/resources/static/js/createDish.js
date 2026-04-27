import {
  bindFormSubmit,
  getInputValue,
  getNumberInputValue,
  redirectTo,
  request,
} from "./modules/appShared.js";

document.addEventListener("DOMContentLoaded", () => {
  // 攔截表單 submit，改為 AJAX 新增餐點。
  bindFormSubmit("createForm", createDish);

  // 返回按鈕：回到目前餐廳的菜單列表。
  const previousButton = document.getElementById("previousBtn");
  previousButton?.addEventListener("click", previousPage);
});

/** 導向指定餐廳的菜單頁。 */
function redirectToDishesPage(restaurantId) {
  redirectTo(`/dinnerHome/restaurants/${restaurantId}/dishes`);
}

/** 送出新增餐點資料。 */
async function createDish() {
  // 整理表單欄位為 API 所需 JSON。
  const restaurantId = getInputValue("restaurantId");
  const dishJson = {
    restaurantId,
    price: getNumberInputValue("price", 0),
    dishName: getInputValue("dishName"),
  };

  // 呼叫新增餐點 API。
  const response = await request("/dishes", {
    method: "POST",
    jsonBody: dishJson,
  });
  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("餐點新增成功！", () => {
      redirectToDishesPage(restaurantId);
    });
  } else {
    window.showAppModal(`新增失敗（${response.status}）`);
  }
}

/** 返回目前餐廳菜單頁。 */
function previousPage() {
  const restaurantId = getInputValue("restaurantId");
  redirectToDishesPage(restaurantId);
}