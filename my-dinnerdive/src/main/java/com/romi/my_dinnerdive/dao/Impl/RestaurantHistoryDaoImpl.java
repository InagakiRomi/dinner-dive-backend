package com.romi.my_dinnerdive.dao.Impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.romi.my_dinnerdive.dao.RestaurantHistoryDao;
import com.romi.my_dinnerdive.dto.RestaurantHistoryQueryParams;
import com.romi.my_dinnerdive.model.Restaurant;
import com.romi.my_dinnerdive.model.RestaurantHistory;
import com.romi.my_dinnerdive.rowmapper.RestaurantHistoryRowMapper;

@Repository
public class RestaurantHistoryDaoImpl implements RestaurantHistoryDao {

    /** 白名單排序欄位 */
    private static final Map<String, String> ORDER_BY_MAPPING = Map.of(
            "history_id", "history_id",
            "restaurant_name", "restaurant_name",
            "category", "category",
            "selected_at", "selected_at"
    );

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void createHistory(Restaurant restaurant) {
        String sql = "INSERT INTO restaurant_history " +
                "(group_id, restaurant_id, restaurant_name, category, selected_at) " +
                "VALUES (:groupId, :restaurantId, :restaurantName, :category, :selectedAt)";

        Map<String, Object> map = new HashMap<>();
        map.put("groupId", restaurant.getGroupId());
        map.put("restaurantId", restaurant.getRestaurantId());
        map.put("restaurantName", restaurant.getRestaurantName());
        map.put("category", restaurant.getCategory().name());
        map.put("selectedAt", new Date());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    @SuppressWarnings("null")
    public Integer countHistory(Integer groupId) {
        String sql = "SELECT count(*) FROM restaurant_history WHERE group_id = :groupId";
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        return namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
    }

    @Override
    public List<RestaurantHistory> getHistories(RestaurantHistoryQueryParams queryParams) {
        // 排序欄位與方向防呆
        String orderBy = ORDER_BY_MAPPING.getOrDefault(queryParams.getOrderBy(), "selected_at");
        String sort = "DESC".equalsIgnoreCase(queryParams.getSort()) ? "DESC" : "ASC";

        String sql = "SELECT history_id, restaurant_id, restaurant_name, category, selected_at " +
                "FROM restaurant_history " +
                "WHERE group_id = :groupId " +
                "ORDER BY " + orderBy + " " + sort + " " +
                "LIMIT :limit OFFSET :offset";

        Map<String, Object> map = new HashMap<>();
        map.put("groupId", queryParams.getGroupId());
        map.put("limit", queryParams.getLimit());
        map.put("offset", queryParams.getOffset());

        return namedParameterJdbcTemplate.query(sql, map, new RestaurantHistoryRowMapper());
    }
}
