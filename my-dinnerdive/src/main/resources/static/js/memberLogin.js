import getAll from './modules/memberDataBuilder.js';

// 等待整個網頁載入完成後再執行邏輯
document.addEventListener("DOMContentLoaded", function (){
    const preventForm = document.getElementById("loginForm");
    // 當表單送出時，先攔截住預設行為，再呼叫自訂的處理邏輯
    preventForm.addEventListener("submit", preventFormSubmit);

    function preventFormSubmit(event) {
        event.preventDefault();  // 防止表單送出時整個頁面刷新
        memberLogin();        // 改為用 JavaScript 處理註冊流程
    }
})

function memberLogin(){
    // 將使用者輸入的帳號與密碼組合成一個物件（用展開語法組成 JSON）
    var memberJson = {
        ...getAll.getUsername(),
        ...getAll.getPassword()
    }

    // 將資料送出給後端 API
    fetch('/users/login', {
        method: "POST",
        headers: getAll.getHeaders(),           // 設定標頭，例如 Content-Type: application/json
        body: JSON.stringify(memberJson) // 將 JavaScript 物件轉為 JSON 字串送出
    })
    .then((response) => {
        if (response.ok) {
            alert("登入成功！");
            window.location.href = "/dinnerHome/randomRestaurant";
        } else {
            alert("尚未註冊或密碼輸入錯誤");
        }
    })
    .catch((error) => {
        alert("系統發生錯誤！");
    })
}