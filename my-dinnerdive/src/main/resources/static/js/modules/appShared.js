// 共用網路錯誤訊息：所有 request 預設用這句。
const DEFAULT_NETWORK_ERROR_MESSAGE = "系統發生錯誤（網路或連線異常）！";
// 共用 JSON 標頭：避免每支檔案重複定義。
const JSON_HEADERS = Object.freeze({
  "Content-Type": "application/json",
  Accept: "application/json",
});

/** 將任意值轉成數字；若失敗則回傳 fallback。 */
function normalizeNumber(value, fallback) {
  const normalized = Number(value);
  return Number.isFinite(normalized) ? normalized : fallback;
}

/** 取得 JSON 請求標頭，可額外覆蓋欄位。 */
export function getJsonHeaders(extraHeaders = {}) {
  return {
    ...JSON_HEADERS,
    ...extraHeaders,
  };
}

/** 判斷是否為登入/授權失敗狀態碼。 */
export function isAuthError(status) {
  return status === 401 || status === 403;
}

/** 依 id 取得元素，若不存在直接丟錯。 */
export function getElementByIdOrThrow(id) {
  const element = document.getElementById(id);
  if (!element) {
    throw new Error(`找不到元素: #${id}`);
  }
  return element;
}

/** 依 id 讀取欄位值，元素不存在時回傳預設值。 */
export function getInputValue(id, defaultValue = "") {
  const element = document.getElementById(id);
  if (!element) {
    return defaultValue;
  }
  return element.value;
}

/** 依 id 讀取數字欄位值，解析失敗回傳 fallback。 */
export function getNumberInputValue(id, fallback = 0) {
  return normalizeNumber(getInputValue(id, fallback), fallback);
}

/** 統一導頁方法，避免各檔案重複操作 location。 */
export function redirectTo(path) {
  window.location.href = path;
}

/** 將物件轉成 query string，自動略過空值。 */
export function createQueryString(params) {
  const query = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") {
      return;
    }
    query.append(key, value);
  });

  return query.toString();
}

/** 綁定表單 submit，統一攔截預設送出行為。 */
export function bindFormSubmit(formId, onSubmit) {
  const formElement = document.getElementById(formId);
  if (!formElement) {
    return;
  }

  formElement.addEventListener("submit", async (event) => {
    event.preventDefault();
    await onSubmit(event, formElement);
  });
}

/** 批次綁定 change 事件（常用於篩選條件切換）。 */
export function bindChangeEvents(ids, handler) {
  ids.forEach((id) => {
    const element = document.getElementById(id);
    if (!element) {
      return;
    }
    element.addEventListener("change", handler);
  });
}

/**
 * 統一 HTTP 請求入口：
 * - 自動處理 JSON body/headers
 * - 連線失敗時顯示彈窗並回傳 null
 */
export async function request(
  url,
  { method = "GET", jsonBody, headers = {}, ...rest } = {},
  networkErrorMessage = DEFAULT_NETWORK_ERROR_MESSAGE,
) {
  const requestOptions = {
    method,
    ...rest,
  };

  if (jsonBody !== undefined) {
    requestOptions.headers = getJsonHeaders(headers);
    requestOptions.body = JSON.stringify(jsonBody);
  } else if (Object.keys(headers).length > 0) {
    requestOptions.headers = headers;
  }

  try {
    return await fetch(url, requestOptions);
  } catch (error) {
    console.error("HTTP 請求失敗:", error);
    window.showAppModal(networkErrorMessage);
    return null;
  }
}

/**
 * 建立分頁控制器：
 * - 管理 offset/limit/total
 * - 控制上一頁/下一頁按鈕狀態
 * - 同步頁碼顯示
 */
export function createPaginationController({
  prevButtonId,
  nextButtonId,
  pageInfoSelector,
  onPageChange,
}) {
  const prevButton = document.getElementById(prevButtonId);
  const nextButton = document.getElementById(nextButtonId);
  const pageInfoElement = document.querySelector(pageInfoSelector);

  // 目前查詢位移、每頁筆數、總筆數。
  let offset = 0;
  let limit = 1;
  let total = 0;

  /** 根據目前分頁資料更新頁碼文字與按鈕狀態。 */
  function updatePagerState() {
    if (!pageInfoElement) {
      return;
    }

    const safeLimit = Math.max(1, normalizeNumber(limit, 1));
    const safeTotal = Math.max(0, normalizeNumber(total, 0));
    const totalPages = Math.max(1, Math.ceil(safeTotal / safeLimit));
    const currentPage = Math.min(
      totalPages,
      Math.floor(normalizeNumber(offset, 0) / safeLimit) + 1,
    );

    pageInfoElement.textContent = `第${currentPage}頁 / 共${totalPages}頁`;

    if (prevButton) {
      prevButton.disabled = currentPage <= 1;
    }

    if (nextButton) {
      nextButton.disabled = currentPage >= totalPages;
    }
  }

  // 上一頁：僅在非第一頁時觸發查詢。
  prevButton?.addEventListener("click", async () => {
    if (offset < limit) {
      return;
    }
    offset -= limit;
    await onPageChange(offset);
  });

  // 下一頁：僅在尚未到最後一頁時觸發查詢。
  nextButton?.addEventListener("click", async () => {
    if (offset >= total - limit) {
      return;
    }
    offset += limit;
    await onPageChange(offset);
  });

  return {
    /** 取得目前 offset（供 API 查詢使用）。 */
    getOffset() {
      return offset;
    },
    /** 重設分頁回第一頁。 */
    reset() {
      offset = 0;
      updatePagerState();
    },
    /** 以後端回傳資料同步分頁狀態。 */
    sync(meta = {}) {
      offset = Math.max(0, normalizeNumber(meta.offset, 0));
      limit = Math.max(1, normalizeNumber(meta.limit, 1));
      total = Math.max(0, normalizeNumber(meta.total, 0));
      updatePagerState();
    },
  };
}
