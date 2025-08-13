/** 取得使用者 ID */
function getUserId(){
    const userId = document.getElementById('userId').value;
    return userId;
}

/** 取得使用者帳號 */
function getUsername(){
    const username =document.getElementById('username').value;
    var usernameVar = {"username": username};
    return usernameVar;
}

/** 取得使用者密碼 */
function getPassword(){
    const userPassword = document.getElementById('userPassword').value;
    var userPasswordVar = {"userPassword": userPassword};
    return userPasswordVar;
}

/** 取得資料最後修改時間 */
function getLastModifiedDate() {
    const lastModifiedDate = document.getElementById('lastModifiedDate').value;
    var lastModifiedDateVar = {"lastModifiedDate": lastModifiedDate};
    return lastModifiedDateVar;
}

/**
 * 回傳標準 HTTP 請求標頭（headers）
 * - 告訴後端我傳的是 JSON（Content-Type）
 * - 告訴後端我想拿 JSON 回來（Accept）
 */
function getHeaders(){
    // 設定格式
    let headers = {
    "Content-Type": "application/json",   // 告訴後端：這是 JSON 格式
    "Accept": "application/json",         // 告訴後端：我希望回傳也是 JSON
    }

    return headers;
}


// 統一導出所有方法，讓其他 JS 檔可以 import 使用

let getAll = {
    getUserId,
    getUsername,
    getPassword,
    getLastModifiedDate,
    getHeaders
}

export default getAll;