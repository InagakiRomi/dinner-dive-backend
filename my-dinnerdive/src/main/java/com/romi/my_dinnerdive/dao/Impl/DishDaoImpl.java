package com.romi.my_dinnerdive.dao.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.romi.my_dinnerdive.dao.DishDao;
import com.romi.my_dinnerdive.dto.DishRequest;
import com.romi.my_dinnerdive.model.Dish;
import com.romi.my_dinnerdive.rowmapper.DishRowmapper;
@Repository
public class DishDaoImpl implements DishDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Dish getDishById(Integer dishId) {
        String sql = "SELECT dish_id, restaurant_id, price, dish_name " +
                     "FROM dishes WHERE dish_id = :dishId";

        Map<String, Object> map = new HashMap<>();
        map.put("dishId", dishId);

        List<Dish> dishList = namedParameterJdbcTemplate.query(sql, map, new DishRowmapper());

        if(!dishList.isEmpty()){
            return dishList.get(0);
        } else{
            return null;
        }
    }

    @Override
    public List<Dish> findByRestaurantId(Integer restaurantId) {
        String sql = "SELECT dish_id, restaurant_id, price, dish_name " +
                     "FROM dishes WHERE restaurant_id = :restaurantId";

        Map<String, Object> map = new HashMap<>();
        map.put("restaurantId", restaurantId);

        List<Dish> dishList = namedParameterJdbcTemplate.query(sql, map, new DishRowmapper());

        return dishList;
    }

    @Override
    public Integer createDish(DishRequest dishRequest) {
        String sql = "INSERT INTO dishes (restaurant_id, price, dish_name) " +
                     "VALUES (:restaurantId, :price, :dishName)";

        Map<String, Object> map = new HashMap<>();
        map.put("restaurantId", dishRequest.getRestaurantId());
        map.put("price", dishRequest.getPrice());
        map.put("dishName", dishRequest.getDishName());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        // 檢查是否成功取得主鍵
        Number key = Objects.requireNonNull(keyHolder.getKey(), "找不到 keyHolder 的主鍵，請檢查 SQL 或資料庫設定。");
        return key.intValue();
    }

    @Override
    public void deleteDishById(Integer dishId){
        String sql = "DELETE FROM dishes WHERE dish_id = :dishId";

        Map<String, Object> map = new HashMap<>();
        map.put("dishId", dishId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteByRestaurantId(Integer restaurantId) {
        String sql = "DELETE FROM dishes WHERE restaurant_id = :restaurantId";

        Map<String, Object> map = new HashMap<>();
        map.put("restaurantId", restaurantId);

        namedParameterJdbcTemplate.update(sql, map);
    }
}
