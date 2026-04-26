package com.romi.my_dinnerdive.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romi.my_dinnerdive.dao.RestaurantHistoryDao;
import com.romi.my_dinnerdive.dto.RestaurantHistoryQueryParams;
import com.romi.my_dinnerdive.model.Restaurant;
import com.romi.my_dinnerdive.model.RestaurantHistory;
import com.romi.my_dinnerdive.service.RestaurantHistoryService;

@Service
public class RestaurantHistoryServiceImpl implements RestaurantHistoryService {

    @Autowired
    private RestaurantHistoryDao restaurantHistoryDao;

    @Override
    public void createHistory(Restaurant restaurant) {
        restaurantHistoryDao.createHistory(restaurant);
    }

    @Override
    public Integer countHistory() {
        return restaurantHistoryDao.countHistory();
    }

    @Override
    public List<RestaurantHistory> getHistories(RestaurantHistoryQueryParams queryParams) {
        return restaurantHistoryDao.getHistories(queryParams);
    }
}
