package com.romi.my_dinnerdive.dao.Impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.romi.my_dinnerdive.dao.UserDao;
import com.romi.my_dinnerdive.dto.UserRegisterRequest;
import com.romi.my_dinnerdive.model.User;
import com.romi.my_dinnerdive.rowmapper.UserRowMapper;

@Component
public class UserDaoImpl implements UserDao{
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public User getUserById(Integer userId){
        String sql = "SELECT user_id, group_id, username, user_password, roles, created_date, last_modified_date " +
                     "FROM users WHERE user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (userList.size() > 0) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public User getUserByUsername(String username){
        String sql = "SELECT user_id, group_id, username, user_password, roles, created_date, last_modified_date " +
                     "FROM users WHERE username = :username";

        Map<String, Object> map = new HashMap<>();
        map.put("username", username);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (userList.size() > 0) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {
        String sql = "INSERT INTO users (group_id, username, user_password, roles, created_date, last_modified_date) " +
                    "VALUES (:groupId, :username, :userPassword, :roles, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("groupId", userRegisterRequest.getGroupId());
        map.put("username", userRegisterRequest.getUsername());
        map.put("userPassword", userRegisterRequest.getUserPassword());
        map.put("roles", userRegisterRequest.getRoles().name());

        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

        // 取得資料庫自動產生的主鍵（user_id）
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            return key.intValue();
        } else {
            throw new IllegalStateException("無法取得自動產生的 user_id");
        }
    }

    @Override
    public Integer createGroup(String groupName) {
        String queryNextIdSql = "SELECT COALESCE(MAX(group_id), 0) + 1 FROM user_groups";
        Integer nextGroupId = namedParameterJdbcTemplate.queryForObject(queryNextIdSql, new HashMap<>(), Integer.class);

        String sql = "INSERT INTO user_groups (group_id, group_name, created_date, last_modified_date) " +
                "VALUES (:groupId, :groupName, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        Date now = new Date();
        map.put("groupId", nextGroupId);
        map.put("groupName", groupName);
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

        namedParameterJdbcTemplate.update(sql, map);
        if (nextGroupId != null) {
            return nextGroupId;
        }
        throw new IllegalStateException("無法取得自動產生的 group_id");
    }

    @Override
    @SuppressWarnings("null")
    public boolean hasAdminInGroup(Integer groupId) {
        String sql = "SELECT count(*) FROM users WHERE group_id = :groupId AND roles = 'ADMIN'";
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public void transferAdmin(Integer groupId, Integer currentAdminId, Integer nextAdminId) {
        String demoteSql = "UPDATE users SET roles = 'USER', last_modified_date = :lastModifiedDate " +
                "WHERE user_id = :currentAdminId AND group_id = :groupId AND roles = 'ADMIN'";
        String promoteSql = "UPDATE users SET roles = 'ADMIN', last_modified_date = :lastModifiedDate " +
                "WHERE user_id = :nextAdminId AND group_id = :groupId";

        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("currentAdminId", currentAdminId);
        map.put("nextAdminId", nextAdminId);
        map.put("lastModifiedDate", new Date());

        namedParameterJdbcTemplate.update(demoteSql, map);
        namedParameterJdbcTemplate.update(promoteSql, map);
    }

    @Override
    public String getGroupNameByGroupId(Integer groupId) {
        String sql = "SELECT group_name FROM user_groups WHERE group_id = :groupId";
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);

        List<String> groupNames = namedParameterJdbcTemplate.queryForList(sql, map, String.class);
        if (groupNames.size() > 0) {
            return groupNames.get(0);
        }
        return null;
    }

    @Override
    public List<User> getUsersByGroupId(Integer groupId) {
        String sql = "SELECT user_id, group_id, username, user_password, roles, created_date, last_modified_date " +
                "FROM users WHERE group_id = :groupId ORDER BY roles DESC, user_id ASC";
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        return namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());
    }

    @Override
    public void updateGroupName(Integer groupId, String groupName) {
        String sql = "UPDATE user_groups SET group_name = :groupName, last_modified_date = :lastModifiedDate " +
                "WHERE group_id = :groupId";
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("groupName", groupName);
        map.put("lastModifiedDate", new Date());
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void removeUserFromGroup(Integer userId, Integer groupId) {
        String sql = "UPDATE users SET group_id = NULL, roles = 'USER', last_modified_date = :lastModifiedDate " +
                "WHERE user_id = :userId AND group_id = :groupId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("groupId", groupId);
        map.put("lastModifiedDate", new Date());
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void updateUserGroup(Integer userId, Integer groupId) {
        String sql = "UPDATE users SET group_id = :groupId, last_modified_date = :lastModifiedDate " +
                "WHERE user_id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("groupId", groupId);
        map.put("lastModifiedDate", new Date());
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void clearUserGroup(Integer userId) {
        String sql = "UPDATE users SET group_id = NULL, roles = 'USER', last_modified_date = :lastModifiedDate " +
                "WHERE user_id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("lastModifiedDate", new Date());
        namedParameterJdbcTemplate.update(sql, map);
    }
}