package com.romi.my_dinnerdive.config;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.romi.my_dinnerdive.constant.UserCategory;
import com.romi.my_dinnerdive.dao.UserDao;
import com.romi.my_dinnerdive.model.User;

/** 可以自動在所有的 @Controller 方法中加入共用資料 */
@ControllerAdvice
public class GlobalModelAttribute {

    @Autowired
    private UserDao userDao;

    /**
     * 這個方法會在每一次執行 Controller 裡的任何方法前被自動呼叫，並且將我們要加入的資料進 model 裡
     * @param model ：當前頁面的 Model，用來傳資料給 Thymeleaf
     * @param principal ：Spring Security 提供的登入使用者資訊
     */
    @ModelAttribute
    public void addUserInfoToModel(Model model, Principal principal) {
        // 先注入受限畫面狀態，供共用導覽列切換顯示
        addRestrictedViewStatus(model, principal);

        // 沒有登入就直接跳過
        if (principal == null) {
            return;
        }

        // 把登入使用者名稱放到 model 裡
        addUsernameToModel(model, principal);
        // 把使用者角色資訊放到 model 裡
        addUserRolesToModel(model);
    }

    /** 將使用者名稱放入 model */
    private void addUsernameToModel(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
    }

    /** 將使用者角色清單放入 model */
    private void addUserRolesToModel(Model model) {
        // 從 Spring Security 拿到目前登入者的詳細權限資訊
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 將權限轉成文字，像是「管理員」、「一般用戶」...
        List<String> roleNames = auth.getAuthorities()  // 取得權限（角色）
            .stream()
            .map(GrantedAuthority::getAuthority)        // 轉成字串，例如：ROLE_ADMIN
            .map(this::convertRoleToDisplayName)        // 嘗試轉成中文顯示名稱
            .filter(Optional::isPresent)                // 過濾掉轉換失敗的
            .map(Optional::get)                         // 取得值
            .collect(Collectors.toList());              // 收集成 List

        // 把多個角色用「、」串起來（例如：「管理員、一般用戶」）
        String joinedRoles = String.join("、", roleNames);
        // 存入 model，讓頁面可以用 ${roles} 顯示角色名稱
        model.addAttribute("roles", joinedRoles);
    }

    /**  將 Spring Security 中的角色名稱（如 ROLE_ADMIN）轉換為 UserCategory 對應的中文名稱 */
    private Optional<String> convertRoleToDisplayName(String role) {
        // 去掉開頭的 "ROLE_" → 只留下真正的角色名稱，例如 ADMIN
        String roleName = role.replace("ROLE_", "");
        
        try {
            // 使用 enum 中的對應值找到中文顯示名稱
            UserCategory category = UserCategory.valueOf(roleName);

            // 回傳中文名稱包裝成 Optional
            return Optional.of(category.getDisplayName());

        } catch (IllegalArgumentException ex) {
            // 如果找不到對應的 enum（表示不是我們認得的角色），就回傳空
            return Optional.empty();
        }
    }

    /** 根據登入/群組狀態決定是否顯示受限畫面。 */
    private void addRestrictedViewStatus(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("restrictedView", false);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isGeneralUser = auth != null
                && auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch("ROLE_USER"::equals);

        User user = userDao.getUserByUsername(principal.getName());
        boolean isUserWithoutGroup = isGeneralUser && user != null && Integer.valueOf(1).equals(user.getGroupId());

        model.addAttribute("restrictedView", isUserWithoutGroup);
        if (isUserWithoutGroup) {
            model.addAttribute("restrictedMessage", "還沒有加入任何群組哦請尋找管理員加入群組");
        }
    }
}