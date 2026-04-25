package com.romi.my_dinnerdive.config;

import com.romi.my_dinnerdive.filter.AuthRedirectFilter;
import com.romi.my_dinnerdive.service.Impl.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** 設定網站的登入、登出、哪些頁面要登入才能看等功能 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 用來查資料庫裡使用者帳號密碼的自訂服務
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    // 判斷登入後要不要自動跳轉頁面
    @Autowired
    private AuthRedirectFilter authRedirectFilter;

    // 變數會從 application-dev.yml 中讀取，決定是不是全部 API 都開放不用登入
    @Value("${security.permit-all:false}")
    private boolean permitAll;

    /** 定義一個「白名單」的陣列，裡面列出所有不需要登入認證就可以訪問的網址 */
    private static final String[] SWAGGER_URL_AUTH_WHITELIST = {
            "/swagger-resources",              // Swagger 資源設定
            "/swagger-resources/**",           // 所有 Swagger 子資源
            "/configuration/ui",               // Swagger UI 的設定檔
            "/configuration/security",         // Swagger 安全設定
            "/swagger-ui.html",                // Swagger 的主頁面 (舊版 UI)
            "/webjars/**",                     // 前端使用的 JavaScript/CSS 套件
            "/v3/api-docs/**",                 // Swagger 3 的 API 文件路徑
            "/api/public/**",                  // 所有開放給外部使用的 API（不需登入）
            "/users/login",                    // 登入 API，也不應該需要登入才能用
            "/actuator/*",                     // Spring Boot Actuator 健康檢查等功能
            "/swagger-ui/**"                   // Swagger UI 所有資源（新版 UI）
    };

    /** 設定網站的安全規則，例如哪些網頁需要登入、要去哪裡登入等等 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        
            // 加入登入跳轉規則，插在 Spring 原本處理登入的流程前面
            .addFilterBefore(authRedirectFilter, UsernamePasswordAuthenticationFilter.class)

            // 告訴 Spring 登入用自訂的查帳號密碼邏輯
            .userDetailsService(userDetailsServiceImpl)
            
            // 關掉 CSRF 功能
            .csrf(csrf -> csrf.disable())

            // 設定路徑的授權規則
            .authorizeHttpRequests(auth -> {auth

                // Swagger 白名單
                .requestMatchers(SWAGGER_URL_AUTH_WHITELIST).permitAll()

                // 靜態資源與登入頁面、註冊頁面允許所有人訪問
                .requestMatchers("/css/**", "/js/**", "/images/**", "/dinnerHome").permitAll()

                // 註冊 API 允許所有人存取
                .requestMatchers(HttpMethod.POST, "/users/register").permitAll()

                // 只有管理員可以使用的 API
                .requestMatchers(HttpMethod.DELETE, "/restaurants/{restaurantId}", "/dishes/{dishId}").hasRole("ADMIN");

                // 如果設定成全部開放，就不需要登入
                if (permitAll) {
                    auth
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/**").permitAll();
                }

                // 除了上面允許的，其他網址都需要登入才可以用
                auth.anyRequest().authenticated();
            })

            // 設定登入相關行為
            .formLogin(login -> login
                // 登入時會顯示的頁面
                .loginPage("/dinnerHome")
                // 表單提交的處理 URL
                .loginProcessingUrl("/users/login")
                // 登入成功後自動跳轉的頁面（true 表示每次都固定跳）
                .defaultSuccessUrl("/dinnerHome/randomRestaurant", true)
                // 允許所有人訪問登入頁
                .permitAll()
            )

            // 登出相關設定
            .logout(logout -> logout
                // 登出提交的 URL
                .logoutUrl("/logout")
                // 登出成功後要跳轉的頁面
                .logoutSuccessUrl("/dinnerHome?logout")
                // 允許所有人執行登出
                .permitAll()
            );

        // 最後把這些設定回傳給 Spring 使用
        return http.build();
    }

    /** 設定密碼加密方式，避免明碼存入 DB */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCrypt 加密演算法來存儲密碼
        return new BCryptPasswordEncoder();
    }
}