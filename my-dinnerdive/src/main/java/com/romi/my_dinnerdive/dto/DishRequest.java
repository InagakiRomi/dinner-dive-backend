package com.romi.my_dinnerdive.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "餐點新增請求資料")
public class DishRequest {

    /** 餐點對應餐廳編號 */
    @Schema(description = "餐點所屬餐廳 ID", example = "1")
    @NotNull
    private Integer restaurantId;

    /** 餐點對應餐點價格 */
    @Schema(description = "餐點價格（整數）", example = "120")
    @NotNull
    private Integer price;
    
    /** 餐點名稱 */
    @Schema(description = "餐點名稱", example = "紅燒牛肉麵")
    @NotBlank
    private String dishName;

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }
}
