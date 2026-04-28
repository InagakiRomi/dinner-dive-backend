package com.romi.my_dinnerdive.service.Impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;

import com.romi.my_dinnerdive.dao.UserDao;
import com.romi.my_dinnerdive.dto.UserLoginRequest;
import com.romi.my_dinnerdive.dto.UserRegisterRequest;
import com.romi.my_dinnerdive.logging.LoggingDemo;
import com.romi.my_dinnerdive.model.User;
import com.romi.my_dinnerdive.service.UserService;

@Component
public class UserSericeImpl implements UserService{

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoggingDemo loggingDemo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User getUserById(Integer userId){
        return userDao.getUserById(userId);
    }

    @Override
    @Transactional
    public Integer register(UserRegisterRequest userRegisterRequest){

        Logger logger = loggingDemo.printUserLog();

        // 檢查帳號是否已註冊
        User user = userDao.getUserByUsername(userRegisterRequest.getUsername());
        if(user != null){
            logger.log(Level.WARNING, MessageFormat.format("該帳號 {0} 已經被註冊", userRegisterRequest.getUsername()));
            throw new ResponseStatusException(HttpStatus.CONFLICT, "該帳號已經被註冊");
        }

        if (userRegisterRequest.getRoles() == null) {
            userRegisterRequest.setRoles(com.romi.my_dinnerdive.constant.UserCategory.USER);
        }

        if (userRegisterRequest.getRoles() == com.romi.my_dinnerdive.constant.UserCategory.ADMIN) {
            String defaultGroupName = userRegisterRequest.getUsername() + "的群組";
            Integer groupId = userDao.createGroup(defaultGroupName);
            userRegisterRequest.setGroupId(groupId);
        }

        // 密碼加密後儲存
        String hashedPassword = passwordEncoder.encode(userRegisterRequest.getUserPassword());
        userRegisterRequest.setUserPassword(hashedPassword);

        // 新增使用者
        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        Logger logger = loggingDemo.printUserLog();

        // 查詢帳號是否存在
        User user = userDao.getUserByUsername(userLoginRequest.getUsername());
        if (user == null) {
            logger.log(Level.WARNING, MessageFormat.format("該帳號 {0} 尚未註冊", userLoginRequest.getUsername()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號不存在");
        }

        // 驗證密碼
        if (!passwordEncoder.matches(userLoginRequest.getUserPassword(), user.getUserPassword())) {
            logger.log(Level.WARNING, MessageFormat.format("帳號 {0} 的密碼不正確", userLoginRequest.getUsername()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "密碼錯誤");
        }

        return user;
    }

    @Override
    @Transactional
    public void transferAdmin(Integer nextAdminUserId) {
        User currentUser = getCurrentUser();
        if (currentUser.getRoles() != com.romi.my_dinnerdive.constant.UserCategory.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有管理員可以移轉權限");
        }

        User nextAdmin = userDao.getUserById(nextAdminUserId);
        if (nextAdmin == null || !currentUser.getGroupId().equals(nextAdmin.getGroupId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "目標使用者不存在或不在同群組");
        }

        userDao.transferAdmin(currentUser.getGroupId(), currentUser.getUserId(), nextAdminUserId);
    }

    @Override
    public String getCurrentGroupName() {
        User currentUser = getCurrentUser();
        return userDao.getGroupNameByGroupId(currentUser.getGroupId());
    }

    @Override
    public List<User> getCurrentGroupMembers() {
        User currentUser = getCurrentUser();
        return userDao.getUsersByGroupId(currentUser.getGroupId());
    }

    @Override
    public void updateCurrentGroupName(String groupName) {
        User currentUser = getCurrentUser();
        ensureAdmin(currentUser);

        if (!StringUtils.hasText(groupName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "團隊名稱不可為空");
        }

        userDao.updateGroupName(currentUser.getGroupId(), groupName.trim());
    }

    @Override
    @Transactional
    public void deleteCurrentGroupMember(Integer targetUserId) {
        User currentUser = getCurrentUser();
        ensureAdmin(currentUser);

        if (currentUser.getUserId().equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不可刪除自己");
        }

        User targetUser = userDao.getUserById(targetUserId);
        if (targetUser == null || !currentUser.getGroupId().equals(targetUser.getGroupId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "目標使用者不存在或不在同群組");
        }

        userDao.deleteUserById(targetUserId, currentUser.getGroupId());
    }

    private void ensureAdmin(User user) {
        if (user.getRoles() != com.romi.my_dinnerdive.constant.UserCategory.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有管理員可以進行此操作");
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "尚未登入");
        }

        User user = userDao.getUserByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "使用者不存在");
        }
        return user;
    }

}
