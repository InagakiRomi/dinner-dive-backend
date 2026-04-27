package com.romi.my_dinnerdive.service;

import com.romi.my_dinnerdive.dto.UserLoginRequest;
import com.romi.my_dinnerdive.dto.UserRegisterRequest;
import com.romi.my_dinnerdive.model.User;

/** 使用者服務介面，定義與帳號相關的業務邏輯 */
public interface UserService {

    /** 取得指定 ID 的使用者資訊 */
    User getUserById(Integer userId);

    /** 註冊新使用者 */
    Integer register(UserRegisterRequest userRegisterRequest);

    /** 使用者登入驗證 */
    User login(UserLoginRequest userLoginRequest);

    /** 轉移目前群組的管理權限 */
    void transferAdmin(Integer nextAdminUserId);
}
