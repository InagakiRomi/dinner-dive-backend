package com.romi.my_dinnerdive.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.romi.my_dinnerdive.constant.RestaurantCategory;
import com.romi.my_dinnerdive.model.RestaurantHistory;

/** 歷史紀錄 RowMapper */
public class RestaurantHistoryRowMapper implements RowMapper<RestaurantHistory> {

    @Override
    public RestaurantHistory mapRow(@NonNull ResultSet resultSet, int i) throws SQLException {
        RestaurantHistory history = new RestaurantHistory();

        history.setHistoryId(resultSet.getInt("history_id"));
        history.setRestaurantId(resultSet.getInt("restaurant_id"));
        history.setRestaurantName(resultSet.getString("restaurant_name"));

        String category = resultSet.getString("category");
        history.setCategory(RestaurantCategory.valueOf(category));

        history.setSelectedAt(resultSet.getTimestamp("selected_at"));

        return history;
    }
}
