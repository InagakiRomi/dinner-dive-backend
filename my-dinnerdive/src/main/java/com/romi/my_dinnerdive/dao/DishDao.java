package com.romi.my_dinnerdive.dao;

import java.util.List;

import com.romi.my_dinnerdive.dto.DishRequest;
import com.romi.my_dinnerdive.model.Dish;

/** 定義餐廳餐點所有與資料庫互動的方法 */
public interface DishDao {

    /** 根據餐點 ID 查詢單筆資料 */
    Dish getDishById(Integer dishId);
    
    /** 根據餐廳 ID 查詢餐點資料 */
    List<Dish> findByRestaurantId(Integer restaurantId);

    /** 新增餐點資料，回傳自動產生的餐點 ID */
    Integer createDish(DishRequest dishRequest);

    /** 刪除指定 ID 的餐點資料 */
    void deleteDishById(Integer dishId);

    /** 根據餐廳 ID 刪除該餐廳所有餐點 */
    void deleteByRestaurantId(Integer restaurantId);
}
