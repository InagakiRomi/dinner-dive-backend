import {
  bindFormSubmit,
  getInputValue,
  isAuthError,
  redirectTo,
  request,
} from "./modules/appShared.js";

document.addEventListener("DOMContentLoaded", () => {
  // 攔截登入表單送出，改為 AJAX 登入。
  bindFormSubmit("loginForm", memberLogin);
});

/** 會員登入流程。 */
async function memberLogin() {
  // 整理登入欄位資料。
  const memberJson = {
    username: getInputValue("loginUsername"),
    userPassword: getInputValue("loginPassword"),
  };

  // 呼叫登入 API。
  const response = await request("/users/login", {
    method: "POST",
    jsonBody: memberJson,
  });
  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("登入成功！", () => {
      redirectTo("/dinnerHome/randomRestaurant");
    });
  } else if (isAuthError(response.status)) {
    window.showAppModal("尚未註冊或密碼輸入錯誤");
  } else {
    window.showAppModal(`登入失敗（${response.status}）`);
  }
}