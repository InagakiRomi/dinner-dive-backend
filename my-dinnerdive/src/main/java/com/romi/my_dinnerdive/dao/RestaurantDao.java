package com.romi.my_dinnerdive.dao;

import java.util.List;

import com.romi.my_dinnerdive.dto.RestaurantQueryParams;
import com.romi.my_dinnerdive.dto.RestaurantRequest;
import com.romi.my_dinnerdive.model.Restaurant;

/** 定義餐廳所有與資料庫互動的方法 */
public interface RestaurantDao {
    
    /** 查詢符合條件的餐廳總數（用於分頁計算） */
    Integer countRestaurant(RestaurantQueryParams restaurantQueryParams);

    /** 查詢餐廳列表（支援篩選、排序與分頁） */
    List<Restaurant> getRestaurants(RestaurantQueryParams restaurantQueryParams);

    /** 根據餐廳 ID 查詢單筆資料 */
    Restaurant getRestaurantById(Integer restaurantId, Integer groupId);

    /** 新增餐廳資料，回傳自動產生的餐廳 ID */
    Integer createRestaurant(RestaurantRequest restaurantRequest, Integer groupId);

    /** 更新指定餐廳的所有欄位 */
    void updateRestaurant(Integer restaurantId, RestaurantRequest restaurantRequest, Integer groupId);

    /** 刪除指定 ID 的餐廳資料 */
    void deleteRestaurantById(Integer restaurantId, Integer groupId);

    /** 取得所有符合條件的餐廳 ID 清單 */
    List<Integer> getAllRestaurantIds(RestaurantQueryParams restaurantQueryParams);

    /** 選定一間餐廳，更新選擇次數與時間 */
    void chooseRestaurant(Integer restaurantId, Integer groupId);
}