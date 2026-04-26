(() => {
  /**
   * 全域導頁事件委派：
   * 任何帶有 data-route 的元素被點擊時，都會導向指定路徑。
   * 這樣可避免在 HTML/innerHTML 中重複寫 onclick。
   */
  document.addEventListener("click", (event) => {
    const routeElement = event.target.closest("[data-route]");
    if (!routeElement) {
      return;
    }

    const targetPath = routeElement.getAttribute("data-route");
    if (!targetPath) {
      return;
    }

    event.preventDefault();
    window.location.href = targetPath;
  });
})();
