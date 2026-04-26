package com.romi.my_dinnerdive.service.Impl;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romi.my_dinnerdive.dao.DishDao;
import com.romi.my_dinnerdive.dao.RestaurantDao;
import com.romi.my_dinnerdive.dto.RestaurantQueryParams;
import com.romi.my_dinnerdive.dto.RestaurantRequest;
import com.romi.my_dinnerdive.logging.LoggingDemo;
import com.romi.my_dinnerdive.model.Restaurant;
import com.romi.my_dinnerdive.service.RestaurantHistoryService;
import com.romi.my_dinnerdive.service.RestaurantService;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private LoggingDemo loggingDemo;

    @Autowired
    private RestaurantHistoryService restaurantHistoryService;

    @Autowired
    private DishDao dishDao;

    // 儲存未被抽過的餐廳 ID
    private List<Integer> idList;

    // 標記是否首次抽籤
    private boolean firstRandom = true;

    @Override
    public Integer countRestaurant(RestaurantQueryParams restaurantQueryParams){
        return restaurantDao.countRestaurant(restaurantQueryParams);
    }

    @Override
    public List<Restaurant> getRestaurants(RestaurantQueryParams restaurantQueryParams){
        return restaurantDao.getRestaurants(restaurantQueryParams);
    }

    @Override
    public Restaurant getRestaurantById(Integer restaurantId) {
        return restaurantDao.getRestaurantById(restaurantId);
    }

    @Override
    public Integer createRestaurant(RestaurantRequest restaurantRequest) {
        return restaurantDao.createRestaurant(restaurantRequest);
    }

    @Override
    public void updateRestaurant(Integer restaurantId, RestaurantRequest restaurantRequest){
        restaurantDao.updateRestaurant(restaurantId, restaurantRequest);
    }

    @Override
    @Transactional
    public void deleteRestaurantById(Integer restaurantId){
        dishDao.deleteByRestaurantId(restaurantId);
        restaurantDao.deleteRestaurantById(restaurantId);
    }

    @Override
    public Restaurant getRandomRestaurant(RestaurantQueryParams restaurantQueryParams) {
        Logger logger = loggingDemo.printRandomRestaurantLog();
     
        // 首次抽籤：取得全部待抽清單
        if(firstRandom){             
            idList = restaurantDao.getAllRestaurantIds(restaurantQueryParams);
            firstRandom = false;
        }

        // 從清單中隨機挑一個餐廳 ID 並移除
        int restaurantId = 0;
        Random random = new Random();
        if (!idList.isEmpty()){
            int randomId =random.nextInt(idList.size());
            restaurantId = idList.get(randomId);
            idList.remove(randomId);

            logger.log(Level.FINE,"本次抽到的食物ID為： " + restaurantId);
        }

        // 所有餐廳抽完，清空狀態重來
        if(idList.isEmpty()){
            clearRandomRestaurant();
            logger.log(Level.INFO, "已抽完所有餐廳重新開始抽");
        }

        // 未抽過的餐廳數量
        int lastFood = idList.size();

        // 顯示資訊
        logger.log(Level.FINE,"目前還有 " + lastFood + " 個餐廳可以抽");
        
        return getRestaurantById(restaurantId);
    }

    @Override
    public void clearRandomRestaurant() {
        Logger logger = loggingDemo.printRandomRestaurantLog();
        firstRandom = true;
        logger.log(Level.INFO, "清空抽籤資料");
    }

    @Override
    public List<Integer> getAllRestaurantIds(RestaurantQueryParams restaurantQueryParams){
        return restaurantDao.getAllRestaurantIds(restaurantQueryParams);
    }

    @Override
    @Transactional
    public void chooseRestaurant(Integer restaurantId){
        Restaurant restaurant = restaurantDao.getRestaurantById(restaurantId);
        if (restaurant == null) {
            return;
        }

        clearRandomRestaurant();
        restaurantDao.chooseRestaurant(restaurantId);
        restaurantHistoryService.createHistory(restaurant);
    }
}
