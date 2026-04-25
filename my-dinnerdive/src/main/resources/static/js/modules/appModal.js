(() => {
  // 採用 IIFE 避免污染全域命名空間，只對外暴露 showAppModal。
  const MODAL_ID = "app-message-modal";
  let modalElement;
  let messageElement;
  let closeButton;
  // 記錄本次彈窗關閉後要執行的動作（例如導頁）。
  let pendingCloseHandler = null;

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
        <button type="button" class="btn btn-default app-modal__close-btn">關閉</button>
      </div>
    `;

    messageElement = modalElement.querySelector(".app-modal__message");
    closeButton = modalElement.querySelector(".app-modal__close-btn");

    closeButton.addEventListener("click", closeModal);
    document.body.appendChild(modalElement);
  }

  /** 關閉彈窗，並執行一次性關閉回呼。 */
  function closeModal() {
    if (!modalElement) {
      return;
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
    // 每次開啟都覆蓋前一次的關閉行為，避免舊回呼殘留。
    pendingCloseHandler = typeof onClose === "function" ? onClose : null;
    messageElement.textContent = message ?? "";

    modalElement.classList.add("is-open");
    modalElement.setAttribute("aria-hidden", "false");
    closeButton.focus();
  }

  window.showAppModal = showAppModal;
})();
