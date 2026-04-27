package com.romi.my_dinnerdive.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.romi.my_dinnerdive.constant.UserCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/** 使用者資料模型（對應資料表 users），主要用於 DAO ↔ Service 間的資料傳遞，封裝一筆使用者的所有資料 */
@Entity
@Table(name = "users") // 對應資料表名稱
@Schema(description = "使用者資料模型")
public class User {

    /** 帳號 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "使用者唯一識別 ID", example = "1")
    private Integer userId;

    /** 所屬群組 ID */
    @Schema(description = "所屬群組 ID", example = "1")
    private Integer groupId;

    /** 使用者帳號 */
    @Column(unique = true)
    @Schema(description = "使用者帳號", example = "Yuna001")
    private String username;

    /** 使用者密碼 */
    @JsonIgnore
    @Schema(description = "使用者密碼（回應通常不顯示）", example = "P@ssw0rd123")
    private String userPassword;

    /** 使用者角色 */
    @Enumerated(EnumType.STRING)
    @Schema(description = "使用者角色", example = "USER")
    private UserCategory roles;

    /** 帳號建立日期 */
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "帳號建立時間", example = "2026-04-26T00:00:00.000+08:00")
    private Date createdDate;

    /** 帳號資料更新日期 */
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "帳號最後更新時間", example = "2026-04-26T00:00:00.000+08:00")
    private Date lastModifiedDate;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
