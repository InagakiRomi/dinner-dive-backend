package com.romi.my_dinnerdive.service;

import java.util.List;

import com.romi.my_dinnerdive.dto.RestaurantQueryParams;
import com.romi.my_dinnerdive.dto.RestaurantRequest;
import com.romi.my_dinnerdive.model.Restaurant;

/** 餐廳服務介面，定義與餐廳相關的商業邏輯操作 */
public interface RestaurantService {


    /** 取得符合查詢條件的餐廳總數 */
    Integer countRestaurant(RestaurantQueryParams restaurantQueryParams);

    /** 查詢餐廳清單（支援條件查詢、排序、分頁） */
    List<Restaurant> getRestaurants(RestaurantQueryParams restaurantQueryParams);

    /** 查詢指定餐廳資料 */
    Restaurant getRestaurantById(Integer restaurantId);

    /** 新增餐廳 */
    Integer createRestaurant(RestaurantRequest restaurantRequest);

    /** 取得同群組新增餐廳時下一個顯示排序 */
    Integer getNextGroupDisplayOrder();

    /** 修改指定餐廳資料 */
    void updateRestaurant(Integer restaurantId, RestaurantRequest restaurantRequest);

    /** 刪除指定餐廳 */
    void deleteRestaurantById(Integer restaurantId);

    /** 取得所有符合條件的餐廳 ID */
    List<Integer> getAllRestaurantIds(RestaurantQueryParams restaurantQueryParams);

    /** 隨機抽出尚未抽過的餐廳 */
    Restaurant getRandomRestaurant(RestaurantQueryParams restaurantQueryParams);

    /** 清空抽籤狀態（讓下次重新抽餐廳） */
    void clearRandomRestaurant();

    /** 指定選擇一間餐廳，並更新其選擇紀錄 */
    void chooseRestaurant(Integer restaurantId);
}