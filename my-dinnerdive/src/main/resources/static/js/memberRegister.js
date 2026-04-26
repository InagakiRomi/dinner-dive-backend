import { bindFormSubmit, redirectTo, request } from "./modules/appShared.js";

// 等待整個網頁載入完成後再執行邏輯
document.addEventListener("DOMContentLoaded", function () {
  // 共用 submit 綁定：攔截預設行為並改成 AJAX。
  bindFormSubmit("registerForm", async (_event, registerForm) => {
    await memberRegister(registerForm);
  });
});

/** 註冊使用者的主邏輯 */
async function memberRegister(registerForm) {
  // 從註冊表單讀取帳號、密碼欄位。
  const usernameInput = registerForm.querySelector("#registerUsername");
  const passwordInput = registerForm.querySelector("#registerPassword");

  if (!usernameInput || !passwordInput) {
    window.showAppModal("註冊欄位讀取失敗，請重新整理頁面後再試一次。");
    return;
  }

  // 將欄位組成後端 API 所需的 JSON。
  const memberJson = {
    username: usernameInput.value,
    userPassword: passwordInput.value,
  };

  // 將資料送出給後端 API
  const response = await request("/users/register", {
    method: "POST",
    jsonBody: memberJson,
  });

  if (!response) {
    return;
  }

  if (response.ok) {
    window.showAppModal("註冊成功！", () => {
      redirectTo("/dinnerHome");
    });
    return;
  }

  // 失敗時優先嘗試讀取後端回傳訊息。
  let errorMessage = "註冊失敗，請稍後再試。";
  const errorData = await response.json().catch(() => null);

  if (typeof errorData?.error === "string" && errorData.error.trim()) {
    errorMessage = errorData.error;
  } else if (errorData && typeof errorData === "object") {
    // 驗證錯誤會是欄位對應訊息的 JSON，合併成單一提示字串
    const fieldErrors = Object.values(errorData).filter(
      (message) => typeof message === "string" && message.trim(),
    );

    if (fieldErrors.length > 0) {
      errorMessage = fieldErrors.join("\n");
    }
  }

  window.showAppModal(errorMessage);
}