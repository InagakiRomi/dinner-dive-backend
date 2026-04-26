package com.romi.my_dinnerdive.dto;

import java.util.Date;

import com.romi.my_dinnerdive.model.RestaurantHistory;

import io.swagger.v3.oas.annotations.media.Schema;

/** 歷史紀錄回應格式 */
@Schema(description = "抽選歷史紀錄查詢回應資料")
public class RestaurantHistoryResponse {

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
    @Schema(description = "餐廳分類顯示名稱", example = "主食")
    private String category;

    /** 選擇時間 */
    @Schema(description = "該筆歷史紀錄的抽中時間", example = "2026-04-26T00:00:00.000+08:00")
    private Date selectedAt;

    public RestaurantHistoryResponse(RestaurantHistory history) {
        this.historyId = history.getHistoryId();
        this.restaurantId = history.getRestaurantId();
        this.restaurantName = history.getRestaurantName();
        this.category = history.getCategory().getDisplayName();
        this.selectedAt = history.getSelectedAt();
    }

    public Integer getHistoryId() {
        return historyId;
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

    public Date getSelectedAt() {
        return selectedAt;
    }
}
