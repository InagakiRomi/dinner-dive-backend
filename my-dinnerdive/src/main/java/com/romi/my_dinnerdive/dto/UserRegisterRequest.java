package com.romi.my_dinnerdive.dto;

import com.romi.my_dinnerdive.constant.UserCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** 用來接收前端送出的註冊資訊，包含帳號、密碼與使用者角色，會在進入 Controller 前進行格式驗證 */
@Schema(description = "使用者註冊請求資料")
public class UserRegisterRequest {
    
    /** 使用者帳號 */
    @Schema(description = "使用者帳號（僅英文與數字）", example = "romi001")
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "帳號只能包含英文與數字")
    private String username;

    /** 使用者密碼 */
    @Schema(description = "使用者密碼", example = "P@ssw0rd123")
    @NotBlank
    private String userPassword;

    /** 使用者角色 */
    @Schema(description = "使用者角色，可填 ADMIN、USER、GUEST", example = "USER")
    private UserCategory roles;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public UserCategory getRoles() {
        return roles;
    }
    public void setRoles(UserCategory roles) {
        this.roles = roles;
    }
}
