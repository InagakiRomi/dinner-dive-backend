package com.romi.my_dinnerdive.dao;

import com.romi.my_dinnerdive.dto.UserRegisterRequest;
import com.romi.my_dinnerdive.model.User;

/** 定義與 users 資料表相關的資料操作方法 */
public interface UserDao {
    
    /** 根據帳號名稱查詢使用者資料 */
    User getUserById(Integer userId);

    /** 根據電子郵件獲取使用者資訊 */
    User getUserByUsername(String username);

    /** 建立新使用者帳號 */
    Integer createUser(UserRegisterRequest userRegisterRequest);

    /** 建立新群組並回傳 group_id */
    Integer createGroup(String groupName);

    /** 同群組目前是否已有管理員 */
    boolean hasAdminInGroup(Integer groupId);

    /** 轉移群組管理權 */
    void transferAdmin(Integer groupId, Integer currentAdminId, Integer nextAdminId);

    /** 取得群組名稱 */
    String getGroupNameByGroupId(Integer groupId);

    /** 取得群組內所有成員 */
    java.util.List<User> getUsersByGroupId(Integer groupId);

    /** 修改群組名稱 */
    void updateGroupName(Integer groupId, String groupName);

    /** 刪除群組成員 */
    void deleteUserById(Integer userId, Integer groupId);
}
