# 🍽️ Dinner Dive - 個人後端實戰專案

## 🧠 專案介紹

Dinner Dive 是一款以「幫助使用者解決吃飯選擇障礙」為核心的餐廳抽籤系統。使用者可以建立個人化的餐廳清單，並透過隨機抽選機制，快速決定今天要吃什麼。

系統提供完整的 CRUD 功能（新增、修改、刪除、查詢餐廳、餐點資料），並結合隨機抽籤，讓使用者能以互動方式選擇餐廳。

---

## 🔍 技術亮點

- 使用 **Spring Boot + RESTful API** 架構開發後端，將功能模組化為 Controller / Service / DAO，方便後續維護與單元測試。
- 以 **JDBC Template 搭配 SQL 動態拼接** 實作彈性查詢，支援多條件搜尋、分頁與自訂排序，提升資料查詢效能與靈活度。
- 結合 **List 狀態控制** 與重置機制，確保每次抽選結果不重複。
- 整合 **Thymeleaf + 靜態 JS**，讓前端能動態渲染資料庫內容，並與後端 API 串接。

---

## 🔧 已實作功能

- 餐廳資料管理（新增 / 查詢 / 修改 / 刪除）
- 支援多條件查詢（分類、關鍵字、排序、分頁）
- 隨機餐廳抽選邏輯（含抽過排除與分類篩選）
- 登入、註冊帳號
- 功能權限管理

---

## 🖼️ 介面展示

<img width="1919" height="859" alt="image01" src="https://github.com/user-attachments/assets/fd5872d5-b37e-44e4-a031-b5bdf1200c11" />
<img width="1919" height="864" alt="image02" src="https://github.com/user-attachments/assets/c0c5e8c4-7c42-4544-a1be-fc7f522a1ec9" />
<img width="1891" height="856" alt="image03" src="https://github.com/user-attachments/assets/1956ea9d-06ae-44c7-8cfd-ecf95f189f84" />
<img width="1894" height="853" alt="image04" src="https://github.com/user-attachments/assets/799996dc-2cb8-4d29-a9c0-cc449071b748" />
<img width="1919" height="864" alt="image05" src="https://github.com/user-attachments/assets/86bbcaf7-eda6-4f20-8007-42ec8d6bf767" />
<img width="1919" height="857" alt="image06" src="https://github.com/user-attachments/assets/ae691395-02e3-4733-ac54-0c57c4e1ab64" />
<img width="1893" height="853" alt="image08" src="https://github.com/user-attachments/assets/5f61cdc7-bdad-41cd-94b2-5f5ea7bfee78" />
<img width="1915" height="861" alt="image07" src="https://github.com/user-attachments/assets/ea08efd4-0920-482f-8894-70436511f7a3" />
<img width="1910" height="861" alt="image09" src="https://github.com/user-attachments/assets/71907003-497b-40be-881f-c2bdd40d4d56" />
<img width="1918" height="862" alt="image10" src="https://github.com/user-attachments/assets/89d1d06a-b244-4829-b954-8ccd7d8f27d1" />

---

## 📃 單元測試

![image](https://github.com/user-attachments/assets/a523eda0-f34f-440d-baa3-e62c92c25412)

---

## 📃 資料庫清單

<img width="836" height="578" alt="image" src="https://github.com/user-attachments/assets/804784dc-b4f4-4b86-9e04-328acba12ba8" />
