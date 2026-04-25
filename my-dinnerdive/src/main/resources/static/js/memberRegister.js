// 等待整個網頁載入完成後再執行邏輯
document.addEventListener("DOMContentLoaded", function () {
  const registerForm = document.getElementById("registerForm");
  const registerFeedback = document.getElementById("registerFeedback");
  const registerFeedbackText = document.getElementById("registerFeedbackText");

  if (!registerForm) {
    return;
  }

  function showRegisterError(message) {
    if (!registerFeedback || !registerFeedbackText) {
      return;
    }

    registerFeedbackText.textContent = message;
    registerFeedback.hidden = false;
  }

  function clearRegisterError() {
    if (!registerFeedback || !registerFeedbackText) {
      return;
    }

    registerFeedbackText.textContent = "";
    registerFeedback.hidden = true;
  }

  // 當表單送出時，先攔截住預設行為，再呼叫自訂的處理邏輯
  registerForm.addEventListener("submit", function (event) {
    event.preventDefault(); // 防止表單送出時整個頁面刷新
    clearRegisterError();
    memberRegister(registerForm, showRegisterError); // 改為用 JavaScript 處理註冊流程
  });
});

/** 註冊使用者的主邏輯 */
async function memberRegister(registerForm, showRegisterError) {
  const usernameInput = registerForm.querySelector("#registerUsername");
  const passwordInput = registerForm.querySelector("#registerPassword");

  if (!usernameInput || !passwordInput) {
    showRegisterError("註冊欄位讀取失敗，請重新整理頁面後再試一次。");
    return;
  }

  const memberJson = {
    username: usernameInput.value,
    userPassword: passwordInput.value,
  };

  // 將資料送出給後端 API
  const response = await fetch("/users/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify(memberJson),
  }).catch((error) => {
    console.error("註冊時發生錯誤:", error);
    showRegisterError("系統發生錯誤（網路或連線異常）！");
    return null;
  });

  if (!response) {
    return;
  }

  if (response.ok) {
    alert("註冊成功！");
    window.location.href = "/dinnerHome";
    return;
  }

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

  showRegisterError(errorMessage);
}