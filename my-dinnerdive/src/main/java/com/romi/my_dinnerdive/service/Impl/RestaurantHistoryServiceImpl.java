package com.romi.my_dinnerdive.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.romi.my_dinnerdive.dao.UserDao;
import com.romi.my_dinnerdive.dao.RestaurantHistoryDao;
import com.romi.my_dinnerdive.dto.RestaurantHistoryQueryParams;
import com.romi.my_dinnerdive.model.Restaurant;
import com.romi.my_dinnerdive.model.RestaurantHistory;
import com.romi.my_dinnerdive.service.RestaurantHistoryService;

@Service
public class RestaurantHistoryServiceImpl implements RestaurantHistoryService {

    @Autowired
    private RestaurantHistoryDao restaurantHistoryDao;

    @Autowired
    private UserDao userDao;

    @Override
    public void createHistory(Restaurant restaurant) {
        restaurantHistoryDao.createHistory(restaurant);
    }

    @Override
    public Integer countHistory() {
        return restaurantHistoryDao.countHistory(getCurrentGroupId());
    }

    @Override
    public List<RestaurantHistory> getHistories(RestaurantHistoryQueryParams queryParams) {
        queryParams.setGroupId(getCurrentGroupId());
        return restaurantHistoryDao.getHistories(queryParams);
    }

    private Integer getCurrentGroupId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            return 1;
        }

        com.romi.my_dinnerdive.model.User user = userDao.getUserByUsername(authentication.getName());
        if (user == null) {
            return 1;
        }
        return user.getGroupId();
    }
}
