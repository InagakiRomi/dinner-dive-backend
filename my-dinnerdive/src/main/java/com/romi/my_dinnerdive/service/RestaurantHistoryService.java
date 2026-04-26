package com.romi.my_dinnerdive.service;

import java.util.List;

import com.romi.my_dinnerdive.dto.RestaurantHistoryQueryParams;
import com.romi.my_dinnerdive.model.Restaurant;
import com.romi.my_dinnerdive.model.RestaurantHistory;

/** 定義歷史紀錄服務方法 */
public interface RestaurantHistoryService {

    /** 新增歷史紀錄 */
    void createHistory(Restaurant restaurant);

    /** 查詢歷史紀錄總數 */
    Integer countHistory();

    /** 查詢歷史紀錄列表 */
    List<RestaurantHistory> getHistories(RestaurantHistoryQueryParams queryParams);
}
