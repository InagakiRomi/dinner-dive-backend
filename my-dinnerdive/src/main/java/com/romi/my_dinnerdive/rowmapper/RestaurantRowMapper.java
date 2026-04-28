package com.romi.my_dinnerdive.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.romi.my_dinnerdive.constant.RestaurantCategory;
import com.romi.my_dinnerdive.model.Restaurant;

/**
 * RowMapper 實作：將 ResultSet 中的資料轉成 Restaurant 物件
 * <p>
 * 用於 NamedParameterJdbcTemplate 查詢餐廳時的資料轉換邏輯
 */
public class RestaurantRowMapper implements RowMapper<Restaurant> {

    /**
     * 將資料庫查詢結果的單筆 row 映射為 Restaurant 實體
     *
     * @param resultSet ：查詢結果
     * @param i ：當前 row 的索引（可忽略）
     * @return 對應的 Restaurant 物件
     */
    @Override
    public Restaurant mapRow(@NonNull ResultSet resultSet, int i) throws SQLException {
        Restaurant restaurant = new Restaurant();

        restaurant.setRestaurantId(resultSet.getInt("restaurant_id"));
        restaurant.setGroupId(resultSet.getInt("group_id"));
        restaurant.setGroupDisplayOrder(resultSet.getInt("group_display_order"));
        restaurant.setRestaurantName(resultSet.getString("restaurant_name"));

        // 餐廳分類轉為 enum
        String categoryStr = resultSet.getString("category");
        RestaurantCategory category = RestaurantCategory.valueOf(categoryStr);
        restaurant.setCategory(category);

        restaurant.setImageUrl(resultSet.getString("image_url"));
        restaurant.setVisitedCount(resultSet.getInt("visited_count"));
        restaurant.setLastSelectedAt(resultSet.getTimestamp("last_selected_at"));
        restaurant.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        restaurant.setNote(resultSet.getString("note"));

        return restaurant;
    }
}