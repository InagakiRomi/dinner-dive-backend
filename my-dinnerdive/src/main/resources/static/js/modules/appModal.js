(() => {
  // 採用 IIFE 避免污染全域命名空間，只對外暴露 showAppModal / showAppConfirm。
  const MODAL_ID = "app-message-modal";
  let modalElement;
  let titleElement;
  let messageElement;
  let primaryButton;
  let cancelButton;
  // 記錄本次彈窗關閉後要執行的動作（例如導頁）。
  let pendingCloseHandler = null;
  // 記錄確認視窗 Promise 的 resolve，用於回傳使用者選擇。
  let pendingConfirmResolver = null;

  /** 若彈窗尚未建立，首次呼叫時動態建立 DOM。 */
  function ensureModal() {
    if (modalElement) {
      return;
    }

    modalElement = document.createElement("div");
    modalElement.id = MODAL_ID;
    modalElement.className = "app-modal";
    modalElement.setAttribute("aria-hidden", "true");

    modalElement.innerHTML = `
      <div class="app-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="app-modal-title">
        <h2 id="app-modal-title" class="app-modal__title">提示</h2>
        <p class="app-modal__message"></p>
        <div class="app-modal__actions">
          <button type="button" class="btn btn-default app-modal__cancel-btn">取消</button>
          <button type="button" class="btn btn-default app-modal__primary-btn">關閉</button>
        </div>
      </div>
    `;

    titleElement = modalElement.querySelector("#app-modal-title");
    messageElement = modalElement.querySelector(".app-modal__message");
    primaryButton = modalElement.querySelector(".app-modal__primary-btn");
    cancelButton = modalElement.querySelector(".app-modal__cancel-btn");

    document.body.appendChild(modalElement);
  }

  function settleConfirm(value) {
    if (typeof pendingConfirmResolver === "function") {
      pendingConfirmResolver(value);
    }
    pendingConfirmResolver = null;
  }

  function showInfoAction() {
    titleElement.textContent = "提示";
    primaryButton.textContent = "關閉";
    cancelButton.style.display = "none";
    primaryButton.onclick = () => closeModal({ settleConfirmAsCancel: true });
    cancelButton.onclick = null;
  }

  function showConfirmActions() {
    titleElement.textContent = "確認";
    primaryButton.textContent = "確認";
    cancelButton.style.display = "";
  }

  /** 關閉彈窗，並執行一次性關閉回呼。 */
  function closeModal({ settleConfirmAsCancel = false } = {}) {
    if (!modalElement) {
      return;
    }

    if (settleConfirmAsCancel) {
      settleConfirm(false);
    }

    modalElement.classList.remove("is-open");
    modalElement.setAttribute("aria-hidden", "true");

    const handler = pendingCloseHandler;
    pendingCloseHandler = null;
    if (typeof handler === "function") {
      handler();
    }
  }

  /** 顯示全域訊息彈窗 */
  function showAppModal(message, onClose) {
    if (!document.body) {
      return;
    }

    ensureModal();
    settleConfirm(false);
    showInfoAction();
    // 每次開啟都覆蓋前一次的關閉行為，避免舊回呼殘留。
    pendingCloseHandler = typeof onClose === "function" ? onClose : null;
    messageElement.textContent = message ?? "";

    modalElement.classList.add("is-open");
    modalElement.setAttribute("aria-hidden", "false");
    primaryButton.focus();
  }

  /** 顯示全域確認彈窗，回傳使用者是否確認。 */
  function showAppConfirm(message) {
    if (!document.body) {
      return Promise.resolve(false);
    }

    ensureModal();
    settleConfirm(false);
    pendingCloseHandler = null;
    showConfirmActions();
    messageElement.textContent = message ?? "";

    modalElement.classList.add("is-open");
    modalElement.setAttribute("aria-hidden", "false");
    primaryButton.focus();

    return new Promise((resolve) => {
      pendingConfirmResolver = resolve;

      primaryButton.onclick = () => {
        settleConfirm(true);
        closeModal({ settleConfirmAsCancel: false });
      };

      cancelButton.onclick = () => {
        settleConfirm(false);
        closeModal({ settleConfirmAsCancel: false });
      };
    });
  }

  window.showAppModal = showAppModal;
  window.showAppConfirm = showAppConfirm;
})();
