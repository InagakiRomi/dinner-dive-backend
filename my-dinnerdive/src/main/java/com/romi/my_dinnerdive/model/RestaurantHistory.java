package com.romi.my_dinnerdive.model;

import java.util.Date;

import com.romi.my_dinnerdive.constant.RestaurantCategory;

import io.swagger.v3.oas.annotations.media.Schema;

/** 歷史紀錄模型 */
@Schema(description = "抽選餐廳歷史紀錄模型")
public class RestaurantHistory {

    /** 歷史紀錄 ID */
    @Schema(description = "歷史紀錄唯一識別 ID", example = "1")
    private Integer historyId;

    /** 對應餐廳 ID */
    @Schema(description = "對應的餐廳 ID", example = "22")
    private Integer restaurantId;

    /** 餐廳名稱 */
    @Schema(description = "抽中當下的餐廳名稱快照", example = "乾乾拌拌")
    private String restaurantName;

    /** 餐廳分類 */
    @Schema(description = "抽中當下的餐廳分類快照", example = "MAIN")
    private RestaurantCategory category;

    /** 選擇時間 */
    @Schema(description = "該筆歷史紀錄的抽中時間", example = "2026-04-26T00:00:00.000+08:00")
    private Date selectedAt;

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

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

    public RestaurantCategory getCategory() {
        return category;
    }

    public void setCategory(RestaurantCategory category) {
        this.category = category;
    }

    public Date getSelectedAt() {
        return selectedAt;
    }

    public void setSelectedAt(Date selectedAt) {
        this.selectedAt = selectedAt;
    }
}
