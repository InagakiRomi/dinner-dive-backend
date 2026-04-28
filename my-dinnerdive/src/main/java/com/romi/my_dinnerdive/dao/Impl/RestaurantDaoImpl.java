package com.romi.my_dinnerdive.dao.Impl;

import java.util.Date;
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

import com.romi.my_dinnerdive.dao.RestaurantDao;
import com.romi.my_dinnerdive.dto.RestaurantQueryParams;
import com.romi.my_dinnerdive.dto.RestaurantRequest;
import com.romi.my_dinnerdive.model.Restaurant;
import com.romi.my_dinnerdive.rowmapper.RestaurantRowMapper;

@Repository
public class RestaurantDaoImpl implements RestaurantDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    @SuppressWarnings("null")
    public Integer countRestaurant(RestaurantQueryParams restaurantQueryParams){
        String sql = "SELECT count(*) FROM restaurants WHERE group_id = :groupId";
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", restaurantQueryParams.getGroupId());

        // 加入查詢條件
        sql = addFilteringSql(sql, map, restaurantQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    @Override
    public List<Restaurant> getRestaurants(RestaurantQueryParams restaurantQueryParams){
        String sql = "SELECT restaurant_id, group_id, group_display_order, restaurant_name, category, image_url, visited_count, last_selected_at, updated_at, note " +
                     "FROM restaurants WHERE group_id = :groupId";

        Map<String, Object> map = new HashMap<>();
        map.put("groupId", restaurantQueryParams.getGroupId());

        // 查詢條件
        sql = addFilteringSql(sql, map, restaurantQueryParams);

        // 排序
        sql = sql + " ORDER BY " + restaurantQueryParams.getOrderBy() + " " + restaurantQueryParams.getSort();

        // 分頁
        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit", restaurantQueryParams.getLimit());
        map.put("offset", restaurantQueryParams.getOffset());

        List<Restaurant> restaurantList = namedParameterJdbcTemplate.query(sql, map, new RestaurantRowMapper());
    
        return restaurantList;
    }

    @Override
    public Restaurant getRestaurantById(Integer restaurantId, Integer groupId) {
        String sql = "SELECT restaurant_id, group_id, group_display_order, restaurant_name, category, image_url, visited_count, last_selected_at, updated_at, note " +
                     "FROM restaurants WHERE restaurant_id = :restaurantId AND group_id = :groupId";

        Map<String, Object> map = new HashMap<>();
        map.put("restaurantId", restaurantId);
        map.put("groupId", groupId);

        List<Restaurant> restaurantList = namedParameterJdbcTemplate.query(sql, map, new RestaurantRowMapper());

        if(!restaurantList.isEmpty()){
            return restaurantList.get(0);
        } else{
            return null;
        }
    }

    @Override
    public Integer createRestaurant(RestaurantRequest restaurantRequest, Integer groupId) {
        String sql = "INSERT INTO restaurants (group_id, group_display_order, restaurant_name, category, image_url, last_selected_at, updated_at, note) " +
                     "VALUES (:groupId, :groupDisplayOrder, :restaurantName, :category, :imageUrl, :lastSelectedAt, :updatedAt, :note)";

        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("groupDisplayOrder", restaurantRequest.getGroupDisplayOrder());
        map.put("restaurantName", restaurantRequest.getRestaurantName());
        map.put("category", restaurantRequest.getCategory().name());
        map.put("imageUrl", restaurantRequest.getImageUrl());
        map.put("lastSelectedAt", restaurantRequest.getLastSelectedAt());
        map.put("updatedAt", new Date());
        map.put("note", restaurantRequest.getNote());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        // 檢查是否成功取得主鍵
        Number key = Objects.requireNonNull(keyHolder.getKey(), "找不到 keyHolder 的主鍵，請檢查 SQL 或資料庫設定。");
        return key.intValue();
    }

    @Override
    public Integer getNextGroupDisplayOrder(Integer groupId) {
        String sql = "SELECT COALESCE(MAX(group_display_order), 0) + 1 FROM restaurants WHERE group_id = :groupId";
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        return namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
    }

    public void updateRestaurant(Integer restaurantId, RestaurantRequest restaurantRequest, Integer groupId){
        String sql = "UPDATE restaurants SET group_display_order = :groupDisplayOrder, restaurant_name = :restaurantName, category = :category, " +
                     "visited_count = :visitedCount, last_selected_at = :lastSelectedAt, note = :note, updated_at = :updatedAt, image_url = :imageUrl " +
                     "WHERE restaurant_id = :restaurantId AND group_id = :groupId";

        Map<String, Object> map = new HashMap<>();
        map.put("restaurantId", restaurantId);
        map.put("groupId", groupId);

        map.put("groupDisplayOrder", restaurantRequest.getGroupDisplayOrder());
        map.put("restaurantName", restaurantRequest.getRestaurantName());
        map.put("category", restaurantRequest.getCategory().toString());
        map.put("visitedCount", restaurantRequest.getVisitedCount());
        map.put("lastSelectedAt", restaurantRequest.getLastSelectedAt());
        map.put("note", restaurantRequest.getNote());
        map.put("imageUrl", restaurantRequest.getImageUrl());

        map.put("updatedAt", new Date());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteRestaurantById(Integer restaurantId, Integer groupId){
        String sql = "DELETE FROM restaurants WHERE restaurant_id = :restaurantId AND group_id = :groupId";

        Map<String, Object> map = new HashMap<>();
        map.put("restaurantId", restaurantId);
        map.put("groupId", groupId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    @SuppressWarnings("null")
    public List<Integer> getAllRestaurantIds(RestaurantQueryParams restaurantQueryParams) {
        String sql = "SELECT restaurant_id FROM restaurants WHERE group_id = :groupId";

        Map<String, Object> map = new HashMap<>();
        map.put("groupId", restaurantQueryParams.getGroupId());

        if (restaurantQueryParams.getCategory() != null) {
            sql += " AND category = :category";
            map.put("category", restaurantQueryParams.getCategory());
        }

        sql = addFilteringSql(sql, map, restaurantQueryParams);

        List<Integer> idList = namedParameterJdbcTemplate.queryForList(sql, map, Integer.class);
        
        return idList;
    }

    @Override
    public void chooseRestaurant(Integer restaurantId, Integer groupId){
        String sql = "UPDATE restaurants SET restaurant_name = :restaurantName, category = :category, " +
                     "visited_count = :visitedCount, last_selected_at = :lastSelectedAt, updated_at = :updatedAt " +
                     "WHERE restaurant_id = :restaurantId AND group_id = :groupId";
    
        Restaurant restaurant = getRestaurantById(restaurantId, groupId);

        Map<String, Object> map = new HashMap<>();
        map.put("restaurantId", restaurantId);
        map.put("groupId", groupId);
        map.put("restaurantName", restaurant.getRestaurantName());
        map.put("category", restaurant.getCategory().toString());
        map.put("visitedCount", restaurant.getVisitedCount() +1);  
        map.put("lastSelectedAt", new Date());
        map.put("updatedAt", new Date());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public boolean existsGroupDisplayOrder(Integer groupId, Integer groupDisplayOrder, Integer excludeRestaurantId) {
        String sql = "SELECT COUNT(*) FROM restaurants WHERE group_id = :groupId AND group_display_order = :groupDisplayOrder";
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("groupDisplayOrder", groupDisplayOrder);

        if (excludeRestaurantId != null) {
            sql += " AND restaurant_id <> :excludeRestaurantId";
            map.put("excludeRestaurantId", excludeRestaurantId);
        }

        Integer count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return count != null && count > 0;
    }

    /** 共用查詢條件處理方法，根據參數動態加上 WHERE 條件 */
    private String addFilteringSql(String sql, Map<String, Object> map, RestaurantQueryParams restaurantQueryParams){
        // 查詢條件
        if (restaurantQueryParams.getCategory() != null) {
            sql += " AND category = :category";
            map.put("category", restaurantQueryParams.getCategory().name());
        }

        if (restaurantQueryParams.getSearch() != null) {
            sql += " AND (restaurant_name LIKE :search OR note LIKE :search)";
            map.put("search", "%" + restaurantQueryParams.getSearch() + "%");
        }

        return sql;
    }
}