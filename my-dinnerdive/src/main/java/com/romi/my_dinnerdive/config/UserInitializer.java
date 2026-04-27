package com.romi.my_dinnerdive.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.romi.my_dinnerdive.constant.UserCategory;
import com.romi.my_dinnerdive.model.User;
import com.romi.my_dinnerdive.repository.UserRepository;

/** 用來建立預設的使用者帳號 */
@Component
public class UserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    /** run 方法會在 Spring Boot 啟動完成後自動執行一次 */
    @Override
    public void run(String... args) {

        // 管理員帳號
        if (userRepository.findByUsername("super").isEmpty()) {
            User user = new User();
            user.setUsername("super");
            user.setGroupId(1);
            user.setUserPassword("$2a$10$e2E9fmZ57LDm/TQGkztKcOFqOzkSPcZAcE5djm.W9nuRbvBKB6KpK");
            user.setRoles(UserCategory.ADMIN);
            user.setCreatedDate(new Date());
            user.setLastModifiedDate(new Date());
            userRepository.save(user);
        }

        // 一般使用者帳號
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setGroupId(1);
            user.setUserPassword("$2a$10$IPcuuRROJ4dRttbvQtVm4.w98d5EjmIhjjtoGx5DpTbgPm8y40HXe");
            user.setRoles(UserCategory.USER);
            user.setCreatedDate(new Date());
            user.setLastModifiedDate(new Date());
            userRepository.save(user);
        }

        // 訪客帳號
        if (userRepository.findByUsername("guest").isEmpty()) {
            User user = new User();
            user.setUsername("guest");
            user.setUserPassword("$2a$10$9ZcJtop4hkPDjD/7AtTjj./zB5mJgm7QcCnHeuIvHvRtMVycoLD.2");
            user.setRoles(UserCategory.GUEST);
            user.setCreatedDate(new Date());
            user.setLastModifiedDate(new Date());
            userRepository.save(user);
        }
    }
}