package com.romi.my_dinnerdive.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.romi.my_dinnerdive.constant.UserCategory;

import jakarta.persistence.*;

/** 使用者資料模型（對應資料表 users），主要用於 DAO ↔ Service 間的資料傳遞，封裝一筆使用者的所有資料 */
@Entity
@Table(name = "users") // 對應資料表名稱
public class User {

    /** 帳號 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    /** 使用者帳號 */
    @Column(unique = true)
    private String username;

    /** 使用者密碼 */
    @JsonIgnore
    private String userPassword;

    /** 使用者角色 */
    @Enumerated(EnumType.STRING)
    private UserCategory roles;

    /** 帳號建立日期 */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    /** 帳號資料更新日期 */
    @Temporal(TemporalType.TIMESTAMP)
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
