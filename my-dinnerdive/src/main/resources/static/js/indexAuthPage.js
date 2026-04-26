// 登入首頁互動邏輯：錯誤提示、登出提示、登入/註冊面板切換。
document.addEventListener("DOMContentLoaded", () => {
  showLoginErrorModalIfNeeded();
  setupLogoutFeedback();
  clearLogoutQueryParam();
  setupAuthPanelSwitch();
  setupLoginSubmittingState();
});

/** 若網址帶有登入錯誤訊息區塊，讀取後以彈窗顯示。 */
function showLoginErrorModalIfNeeded() {
  const loginErrorMessage = document.getElementById("loginErrorMessage");
  if (!loginErrorMessage) {
    return;
  }

  const messageText = loginErrorMessage.textContent.trim();
  if (messageText) {
    window.showAppModal(messageText);
  }
  loginErrorMessage.remove();
}

/** 顯示登出提示，數秒後淡出並移除。 */
function setupLogoutFeedback() {
  const logoutFeedback = document.getElementById("logoutFeedback");
  if (!logoutFeedback) {
    return;
  }

  setTimeout(() => {
    logoutFeedback.classList.add("auth-feedback--fade-out");
    setTimeout(() => {
      logoutFeedback.remove();
    }, 600);
  }, 5000);
}

/** 清掉 URL 中的 logout 參數，避免重新整理後重複顯示提示。 */
function clearLogoutQueryParam() {
  const currentUrl = new URL(window.location.href);
  if (!currentUrl.searchParams.has("logout")) {
    return;
  }

  currentUrl.searchParams.delete("logout");
  const nextUrl =
    currentUrl.pathname +
    (currentUrl.search ? currentUrl.search : "") +
    currentUrl.hash;
  window.history.replaceState({}, "", nextUrl);
}

/** 綁定登入/註冊面板切換按鈕。 */
function setupAuthPanelSwitch() {
  const authTitle = document.getElementById("authTitle");
  const loginPanel = document.getElementById("loginPanel");
  const registerPanel = document.getElementById("registerPanel");
  const toRegisterBtn = document.getElementById("toRegisterBtn");
  const toLoginBtn = document.getElementById("toLoginBtn");

  if (!authTitle || !loginPanel || !registerPanel || !toRegisterBtn || !toLoginBtn) {
    return;
  }

  function switchTo(panel) {
    const showLogin = panel === "login";
    loginPanel.classList.toggle("is-active", showLogin);
    registerPanel.classList.toggle("is-active", !showLogin);
    authTitle.textContent = showLogin ? "會員登入" : "會員註冊";
  }

  toRegisterBtn.addEventListener("click", () => {
    switchTo("register");
  });

  toLoginBtn.addEventListener("click", () => {
    switchTo("login");
  });

  switchTo("login");
}

/** 登入送出時切換按鈕為「登入中...」，避免重複提交。 */
function setupLoginSubmittingState() {
  const loginForm = document.querySelector("#loginPanel form");
  if (!loginForm) {
    return;
  }

  const loginSubmitButton = loginForm.querySelector('button[type="submit"]');
  if (!loginSubmitButton) {
    return;
  }

  const originalButtonText = loginSubmitButton.textContent;
  loginForm.addEventListener("submit", () => {
    loginSubmitButton.disabled = true;
    loginSubmitButton.textContent = "登入中...";
    loginSubmitButton.setAttribute("aria-busy", "true");
  });

  // 若使用者切換到註冊再切回登入，仍保持可用且文字正確。
  const toLoginBtn = document.getElementById("toLoginBtn");
  toLoginBtn?.addEventListener("click", () => {
    loginSubmitButton.disabled = false;
    loginSubmitButton.textContent = originalButtonText || "登入";
    loginSubmitButton.removeAttribute("aria-busy");
  });
}
