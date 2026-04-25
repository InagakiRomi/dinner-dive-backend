package com.romi.my_dinnerdive.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 自訂過濾器：
 * <p>
 * 若使用者已登入，並試圖進入登入或註冊介面，則自動導向首頁
 * */
@Component
public class AuthRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 取得目前請求的 URI
        String path = request.getRequestURI();

        // 取得目前使用者的登入狀態
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 判斷是否為已登入狀態（排除匿名用戶）
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());

        // 若已登入且進入登入頁，則導向首頁
        if (isLoggedIn && ("/dinnerHome".equals(path))) {
            response.sendRedirect("/dinnerHome/randomRestaurant");
            return;
        }

        // 不符合導向條件，照正常流程繼續往下執行
        filterChain.doFilter(request, response);
    }
}