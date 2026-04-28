package com.romi.my_dinnerdive.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class ThymeleafControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRenderDinnerHomeWithoutAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @ParameterizedTest
    @CsvSource({
            "/dinnerHome/randomRestaurant,dinnerHome/randomRestaurant",
            "/dinnerHome/noGroup,dinnerHome/noGroup",
            "/dinnerHome/restaurantHistory,dinnerHome/restaurantHistory",
            "/dinnerHome/memberManagement,dinnerHome/memberManagement",
            "/dinnerHome/listRestaurant,dinnerHome/listRestaurant",
            "/dinnerHome/createRestaurant,dinnerHome/createRestaurant"
    })
    void shouldRenderSimpleDinnerHomePagesWhenAuthenticated(String path, String viewName) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(path))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    void shouldRenderUpdateRestaurantPageWithRestaurantModelWhenRestaurantExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/restaurants/{restaurantId}/edit", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("restaurants"))
                .andExpect(view().name("dinnerHome/updateRestaurant"));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    void shouldRenderOrderPageWithRestaurantModelWhenRestaurantExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/restaurants/{restaurantId}/dishes", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("restaurants"))
                .andExpect(view().name("dinnerHome/orderRestaurant"));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    void shouldRenderCreateDishPageWithRestaurantModelWhenRestaurantExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/restaurants/{restaurantId}/dishes/createDish", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("restaurants"))
                .andExpect(view().name("dinnerHome/createDish"));
    }
}
