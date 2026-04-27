package com.romi.my_dinnerdive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.romi.my_dinnerdive.model.Restaurant;
import com.romi.my_dinnerdive.service.RestaurantService;

/** 頁面導向控制器（負責導到 Thymeleaf HTML 畫面） */
@Controller
public class ThymeleafController {

    @Autowired
    private RestaurantService restaurantService;

    /** 登入頁面 */
    @GetMapping("/dinnerHome")
    public String dinnerHome() {
        return "index";
    }
    
    /** 抽選餐廳頁面 */
    @GetMapping("/dinnerHome/randomRestaurant")
    public String randomRestaurantPage() {
        return "dinnerHome/randomRestaurant";
    }

    /** 未加入群組提示頁面 */
    @GetMapping("/dinnerHome/noGroup")
    public String noGroupPage() {
        return "dinnerHome/noGroup";
    }

    /** 抽選歷史紀錄頁面 */
    @GetMapping("/dinnerHome/restaurantHistory")
    public String restaurantHistoryPage() {
        return "dinnerHome/restaurantHistory";
    }

    /** 成員管理頁面 */
    @GetMapping("/dinnerHome/memberManagement")
    public String memberManagementPage() {
        return "dinnerHome/memberManagement";
    }
    
    /** 餐廳一覽頁面 */
    @GetMapping("dinnerHome/listRestaurant")
    public String readPage() {
        return "dinnerHome/listRestaurant";
    }

    /** 新增餐廳頁面 */
    @GetMapping("dinnerHome/createRestaurant")
    public String createPage() {
        return "dinnerHome/createRestaurant";
    }

    /** 修改餐廳資料頁面 */
    @GetMapping("dinnerHome/restaurants/{restaurantId}/edit")
    public String updatePage(@PathVariable Integer restaurantId, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        model.addAttribute("restaurants", restaurant);
        return "dinnerHome/updateRestaurant";
    }

    /** 點餐頁面 */
    @GetMapping("dinnerHome/restaurants/{restaurantId}/dishes")
    public String orderPage(@PathVariable Integer restaurantId, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        model.addAttribute("restaurants", restaurant);
        return "dinnerHome/orderRestaurant";
    }

    /** 新增餐點頁面 */
    @GetMapping("dinnerHome/restaurants/{restaurantId}/dishes/createDish")
    public String createDishPage(@PathVariable Integer restaurantId, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        model.addAttribute("restaurants", restaurant);
        return "dinnerHome/createDish";
    }
}