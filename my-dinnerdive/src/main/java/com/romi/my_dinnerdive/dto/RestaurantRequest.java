package com.romi.my_dinnerdive.dto;

import java.util.Date;

import com.romi.my_dinnerdive.constant.RestaurantCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** 用於接收前端傳送過來的 JSON 資料，新增或修改餐廳時使用 */
@Schema(description = "餐廳新增或更新請求資料")
public class RestaurantRequest {

    /** 餐廳名稱，不可為空 */
    @Schema(description = "餐廳名稱", example = "阿明牛肉麵")
    @NotBlank
    private String restaurantName;

    /** 餐廳分類 */
    @Schema(description = "餐廳分類，可填 MAIN、SNACK、DRINK", example = "MAIN")
    @NotNull
    private RestaurantCategory category;

    /** 群組內排序 ID（同群組不可重複） */
    @Schema(description = "群組內排序 ID（同群組不可重複）", example = "10")
    @NotNull
    @Min(1)
    private Integer groupDisplayOrder;

    /** 餐廳圖片URL */
    @Schema(description = "餐廳圖片網址", example = "https://example.com/restaurant.jpg")
    private String imageUrl;

    /** 選擇次數 */
    @Schema(description = "被選中的累計次數", example = "0")
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

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public RestaurantCategory getCategory() {
        return category;
    }

    public void setCategory(RestaurantCategory category) {
        this.category = category;
    }

    public Integer getGroupDisplayOrder() {
        return groupDisplayOrder;
    }

    public void setGroupDisplayOrder(Integer groupDisplayOrder) {
        this.groupDisplayOrder = groupDisplayOrder;
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