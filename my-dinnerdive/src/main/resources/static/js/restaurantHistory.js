// 分頁狀態：目前位移、每頁筆數、總筆數
let offset;
let limit;
let total;

document.addEventListener("DOMContentLoaded", function () {
    listHistory(); // 頁面載入後先查一次歷史紀錄

    const historyForm = document.getElementById("historyForm");
    historyForm.addEventListener("submit", function (event) {
        event.preventDefault(); // 攔截表單預設送出，改為 AJAX 查詢
        offset = 0; // 重新查詢時回到第一頁
        listHistory();
    });

    const orderBySelect = document.getElementById("orderBy");
    orderBySelect.addEventListener("change", function () {
        offset = 0; // 變更排序欄位時回到第一頁
        listHistory();
    });

    const sortSelect = document.getElementById("sort");
    sortSelect.addEventListener("change", function () {
        offset = 0; // 變更排序方向時回到第一頁
        listHistory();
    });
});

const prevButton = document.getElementById("prevPage");
prevButton.addEventListener("click", function () {
    if (offset >= limit) {
        offset = offset - limit; // 往前翻一頁
        listHistory();
    }
});

const nextButton = document.getElementById("nextPage");
nextButton.addEventListener("click", function () {
    if (offset < total - limit) {
        offset = offset + limit; // 往後翻一頁
        listHistory();
    }
});

/** 查詢抽選歷史紀錄並渲染表格/分頁資訊 */
async function listHistory() {
    const orderBy = document.getElementById("orderBy").value;
    const sort = document.getElementById("sort").value;

    // 組出查詢參數（排序 + 分頁）
    const params = new URLSearchParams();
    params.append("orderBy", orderBy);
    params.append("sort", sort);
    params.append("offset", offset || 0);

    const url = `/restaurantHistories?${params.toString()}`;

    const response = await fetch(url).catch((error) => {
        console.error("查詢歷史紀錄時發生錯誤:", error);
        window.showAppModal("系統發生錯誤（網路或連線異常）！");
        return null;
    });

    if (!response) {
        return;
    }

    if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
            window.showAppModal("請先登入後再查看歷史紀錄。");
        } else {
            window.showAppModal(`查詢歷史紀錄失敗（${response.status}）`);
        }
        return;
    }

    const result = await response.json();
    // 同步後端回傳的最新分頁資訊
    offset = result.offset;
    limit = result.limit;
    total = result.total;

    const data = result.results;
    const tableBody = document.getElementById("historyTableBody");
    tableBody.innerHTML = "";

    if (data.length === 0) {
        // 沒資料時顯示提示列
        const tr = document.createElement("tr");
        tr.innerHTML = `<td colspan="4">目前沒有抽選歷史紀錄</td>`;
        tableBody.appendChild(tr);
    } else {
        // 有資料時逐筆渲染表格列
        data.forEach((history) => {
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

    const pageInfo = document.querySelector(".switchPageBtn div");
    const currentPage = Math.floor(offset / limit) + 1;
    const totalPages = Math.max(1, Math.ceil(total / limit));
    pageInfo.textContent = `第${currentPage}頁 / 共${totalPages}頁`;

    // 依目前頁數控制上下頁按鈕是否可點擊
    prevButton.disabled = currentPage === 1;
    nextButton.disabled = currentPage === totalPages;
}
