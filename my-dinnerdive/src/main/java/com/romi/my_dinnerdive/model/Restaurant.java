package com.romi.my_dinnerdive.model;

import java.util.Date;

import com.romi.my_dinnerdive.constant.RestaurantCategory;

import io.swagger.v3.oas.annotations.media.Schema;

/** 餐廳資料模型（對應資料表 restaurants），主要用於 DAO ↔ Service 間的資料傳遞，封裝一筆餐廳的所有欄位 */
@Schema(description = "餐廳資料模型")
public class Restaurant {

    /** 餐廳 ID */
    @Schema(description = "餐廳唯一識別 ID", example = "1")
    private Integer restaurantId;

    /** 所屬群組 ID */
    @Schema(description = "所屬群組 ID", example = "1")
    private Integer groupId;

    /** 群組內排序 ID（同群組不可重複） */
    @Schema(description = "群組內排序 ID（同群組不可重複）", example = "10")
    private Integer groupDisplayOrder;

    /** 餐廳名稱 */
    @Schema(description = "餐廳名稱", example = "阿明牛肉麵")
    private String restaurantName;

    /** 餐廳分類 */
    @Schema(description = "餐廳分類", example = "MAIN")
    private RestaurantCategory category;

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

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getGroupDisplayOrder() {
        return groupDisplayOrder;
    }

    public void setGroupDisplayOrder(Integer groupDisplayOrder) {
        this.groupDisplayOrder = groupDisplayOrder;
    }

    public RestaurantCategory getCategory() {
        return category;
    }

    public void setCategory(RestaurantCategory category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getVisitedCount() {
        return visitedCount;
    }

    public void setVisitedCount(Integer visitedCount) {
        this.visitedCount = visitedCount;
    }

    public Date getLastSelectedAt() {
        return lastSelectedAt;
    }

    public void setLastSelectedAt(Date lastSelectedAt) {
        this.lastSelectedAt = lastSelectedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}