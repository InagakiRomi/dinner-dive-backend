package com.romi.my_dinnerdive.rowmapper;

import java.sql.ResultSet;
import org.springframework.lang.NonNull;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.romi.my_dinnerdive.constant.UserCategory;
import com.romi.my_dinnerdive.model.User;

/**
 * RowMapper 實作：將 ResultSet 中的資料轉成 User 物件
 * <p>
 * 用於 JDBC 查詢使用者資料時，將每筆資料列轉成 User 實例
 */
public class UserRowMapper implements RowMapper<User>{
    
    /**
     * 將資料庫查詢結果的單筆 row 映射為 User 實體
     *
     * @param resultSet ：查詢結果
     * @param i ：當前 row 的索引（可忽略）
     * @return 對應的 User 物件
     */
    @Override
    public User mapRow(@NonNull ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setGroupId(resultSet.getInt("group_id"));
        user.setUsername(resultSet.getString("username"));
        
        // 將角色字串轉為 enum
        String rolesStr = resultSet.getString("roles");
        UserCategory roles = UserCategory.valueOf(rolesStr);
        user.setRoles(roles);

        user.setUserPassword(resultSet.getString("user_password"));
        user.setCreatedDate(resultSet.getTimestamp("created_date"));
        user.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));

        return user;
    }
}
