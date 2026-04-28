import {
  bindFormSubmit,
  getInputValue,
  getNumberInputValue,
  redirectTo,
  request,
} from "./modules/appShared.js";

document.addEventListener("DOMContentLoaded", () => {
  // 同步後端帶入的分類預設值，避免 select 顯示錯誤。
  syncCategoryValue();
  // 攔截表單 submit，改為 AJAX 更新資料。
  bindFormSubmit("updateForm", updateRestaurant);
});

/** 同步分類欄位的初始值（從 th:value 帶入）。 */
function syncCategoryValue() {
  const categorySelect = document.getElementById("category");
  if (!categorySelect) {
    return;
  }

  const selectedValue = categorySelect.getAttribute("value");
  if (selectedValue) {
    categorySelect.value = selectedValue;
  }
}

/** 送出餐廳更新資料。 */
async function updateRestaurant() {
  // 讀取隱藏欄位 ID，並整理其他欄位成 JSON。
  const restaurantId = getInputValue("restaurantId");
  const restaurantJson = {
    restaurantName: getInputValue("restaurantName"),
    category: getInputValue("category"),
    groupDisplayOrder: getNumberInputValue("groupDisplayOrder", 0),
    visitedCount: getNumberInputValue("visitedCount", 0),
    lastSelectedAt: getInputValue("lastSelectedAt"),
    note: getInputValue("note"),
    imageUrl: getInputValue("imageUrl"),
  };

  // 呼叫更新 API。
  const response = await request(`/restaurants/${restaurantId}`, {
    method: "PUT",
    jsonBody: restaurantJson,
  });
  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("修改成功！", () => {
      redirectTo("/dinnerHome/listRestaurant");
    });
  } else {
    window.showAppModal(`修改失敗（${response.status}）`);
  }
}