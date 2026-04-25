package com.romi.my_dinnerdive.dto;

import java.util.Date;

import com.romi.my_dinnerdive.model.Restaurant;

import io.swagger.v3.oas.annotations.media.Schema;

/** 將資料庫中的 Restaurant 實體轉換為回傳給前端用的格式，通常用於查詢結果的列表、詳細資料頁 */
@Schema(description = "餐廳查詢回應資料")
public class RestaurantResponse {

    /** 餐廳 ID */
    @Schema(description = "餐廳唯一識別 ID", example = "1")
    private Integer restaurantId;

    /** 餐廳名稱 */
    @Schema(description = "餐廳名稱", example = "阿明牛肉麵")
    private String restaurantName;

    /** 餐廳分類 */
    @Schema(description = "餐廳分類顯示名稱", example = "主食")
    private String  category;

    /** 餐廳圖片URL */
    @Schema(description = "餐廳圖片網址", example = "https://example.com/restaurant.jpg")
    private String imageUrl;

    /** 選擇次數 */
    @Schema(description = "被選中的累計次數", example = "12")
    private Integer visitedCount;

    /** 最後一次選擇時間 */
    @Schema(description = "最後一次被選中的時間", example = "2026-04-26T00:00:00.000+08:00")
    private Date lastSelectedAt;

    /** 最後一次更新時間 */
    @Schema(description = "資料最後更新時間", example = "2026-04-26T00:00:00.000+08:00")
    private Date updatedAt;

    /** 備註欄 */
    @Schema(description = "補充說明或備註", example = "平日中午人較少")
    private String note;

    /**
     * 建構子：將 Restaurant 實體轉換為前端要的回應格式
     * @param restaurant ：從資料庫查出的餐廳實體物件
     */
    public RestaurantResponse(Restaurant restaurant) {
        this.restaurantId = restaurant.getRestaurantId();
        this.restaurantName = restaurant.getRestaurantName();

        // 將 enum 轉為對應的顯示名稱（如 "MAIN" → "主食"）
        this.category = restaurant.getCategory().getDisplayName();

        this.imageUrl = restaurant.getImageUrl();
        this.visitedCount = restaurant.getVisitedCount();
        this.lastSelectedAt = restaurant.getLastSelectedAt();
        this.updatedAt = restaurant.getUpdatedAt();
        this.note = restaurant.getNote();
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getVisitedCount() {
        return visitedCount;
    }

    public Date getLastSelectedAt() {
        return lastSelectedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getNote() {
        return note;
    }
}
