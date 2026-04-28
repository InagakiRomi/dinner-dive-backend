package com.romi.my_dinnerdive.service.Impl;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.romi.my_dinnerdive.dao.DishDao;
import com.romi.my_dinnerdive.dao.RestaurantDao;
import com.romi.my_dinnerdive.dao.UserDao;
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

    // 每個群組各自維護抽籤池，避免跨群組干擾
    private final Map<Integer, List<Integer>> randomPoolByGroup = new ConcurrentHashMap<>();

    @Autowired
    private UserDao userDao;

    @Override
    public Integer countRestaurant(RestaurantQueryParams restaurantQueryParams){
        restaurantQueryParams.setGroupId(getCurrentGroupId());
        return restaurantDao.countRestaurant(restaurantQueryParams);
    }

    @Override
    public List<Restaurant> getRestaurants(RestaurantQueryParams restaurantQueryParams){
        restaurantQueryParams.setGroupId(getCurrentGroupId());
        return restaurantDao.getRestaurants(restaurantQueryParams);
    }

    @Override
    public Restaurant getRestaurantById(Integer restaurantId) {
        return restaurantDao.getRestaurantById(restaurantId, getCurrentGroupId());
    }

    @Override
    public Integer createRestaurant(RestaurantRequest restaurantRequest) {
        Integer groupId = getCurrentGroupId();
        validateUniqueGroupDisplayOrder(groupId, restaurantRequest.getGroupDisplayOrder(), null);
        return restaurantDao.createRestaurant(restaurantRequest, groupId);
    }

    @Override
    public Integer getNextGroupDisplayOrder() {
        return restaurantDao.getNextGroupDisplayOrder(getCurrentGroupId());
    }

    @Override
    public void updateRestaurant(Integer restaurantId, RestaurantRequest restaurantRequest){
        Integer groupId = getCurrentGroupId();
        validateUniqueGroupDisplayOrder(groupId, restaurantRequest.getGroupDisplayOrder(), restaurantId);
        restaurantDao.updateRestaurant(restaurantId, restaurantRequest, groupId);
    }

    @Override
    @Transactional
    public void deleteRestaurantById(Integer restaurantId){
        dishDao.deleteByRestaurantId(restaurantId);
        restaurantDao.deleteRestaurantById(restaurantId, getCurrentGroupId());
    }

    @Override
    public Restaurant getRandomRestaurant(RestaurantQueryParams restaurantQueryParams) {
        Logger logger = loggingDemo.printRandomRestaurantLog();
        Integer groupId = getCurrentGroupId();
        restaurantQueryParams.setGroupId(groupId);
     
        List<Integer> idList = randomPoolByGroup.get(groupId);
        if (idList == null || idList.isEmpty()) {
            idList = restaurantDao.getAllRestaurantIds(restaurantQueryParams);
            randomPoolByGroup.put(groupId, idList);
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
        
        return restaurantDao.getRestaurantById(restaurantId, groupId);
    }

    @Override
    public void clearRandomRestaurant() {
        Logger logger = loggingDemo.printRandomRestaurantLog();
        randomPoolByGroup.remove(getCurrentGroupId());
        logger.log(Level.INFO, "清空抽籤資料");
    }

    @Override
    public List<Integer> getAllRestaurantIds(RestaurantQueryParams restaurantQueryParams){
        restaurantQueryParams.setGroupId(getCurrentGroupId());
        return restaurantDao.getAllRestaurantIds(restaurantQueryParams);
    }

    @Override
    @Transactional
    public void chooseRestaurant(Integer restaurantId){
        Integer groupId = getCurrentGroupId();
        Restaurant restaurant = restaurantDao.getRestaurantById(restaurantId, groupId);
        if (restaurant == null) {
            return;
        }

        clearRandomRestaurant();
        restaurantDao.chooseRestaurant(restaurantId, groupId);
        restaurantHistoryService.createHistory(restaurant);
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

    private void validateUniqueGroupDisplayOrder(Integer groupId, Integer groupDisplayOrder, Integer excludeRestaurantId) {
        if (restaurantDao.existsGroupDisplayOrder(groupId, groupDisplayOrder, excludeRestaurantId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "同群組內 groupDisplayOrder 不可重複"
            );
        }
    }
}
