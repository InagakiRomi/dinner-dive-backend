import {
  bindChangeEvents,
  bindFormSubmit,
  createPaginationController,
  createQueryString,
  getInputValue,
  request,
} from "./modules/appShared.js";

// 抽選歷史列表表格內容容器（tbody）。
const tableBody = document.getElementById("historyTableBody");

// 分頁控制器：統一管理分頁狀態與頁碼顯示。
const pagination = createPaginationController({
  prevButtonId: "prevPage",
  nextButtonId: "nextPage",
  pageInfoSelector: ".switchPageBtn div",
  onPageChange: async () => {
    await listHistory();
  },
});

document.addEventListener("DOMContentLoaded", async () => {
  // 攔截查詢表單送出，改成 AJAX 查詢。
  bindFormSubmit("historyForm", async () => {
    pagination.reset();
    await listHistory();
  });

  // 調整排序條件時，自動回第一頁查詢。
  bindChangeEvents(["orderBy", "sort"], async () => {
    pagination.reset();
    await listHistory();
  });

  await listHistory();
});

/** 查詢抽選歷史資料並同步分頁。 */
async function listHistory() {
  const query = createQueryString({
    orderBy: getInputValue("orderBy"),
    sort: getInputValue("sort"),
    offset: pagination.getOffset(),
  });

  // 呼叫後端 API 取得歷史紀錄分頁結果。
  const response = await request(`/restaurantHistories?${query}`);
  if (!response) {
    return;
  }

  if (!response.ok) {
    window.showAppModal(`查詢歷史紀錄失敗（${response.status}）`);
    return;
  }

  // 同步分頁資訊並渲染表格。
  const result = await response.json();
  pagination.sync(result);
  renderHistoryRows(result.results ?? []);
}

/** 渲染歷史資料表格（含空資料提示）。 */
function renderHistoryRows(records) {
  if (!tableBody) {
    return;
  }

  tableBody.innerHTML = "";

  if (records.length === 0) {
    const tr = document.createElement("tr");
    tr.innerHTML = '<td colspan="4">目前沒有抽選歷史紀錄</td>';
    tableBody.appendChild(tr);
    return;
  }

  records.forEach((history) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${history.historyId}</td>
      <td class="noteCell">${history.restaurantName}</td>
      <td>${history.category}</td>
      <td>${history.selectedAt}</td>
    `;
    tableBody.appendChild(tr);
  });
}
