package com.romi.my_dinnerdive.controller;

import org.junit.jupiter.api.Test;
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
    @Test
    void shouldRenderRandomRestaurantPageWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/randomRestaurant"))
                .andExpect(status().isOk())
                .andExpect(view().name("dinnerHome/randomRestaurant"));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    void shouldRenderNoGroupPageWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/noGroup"))
                .andExpect(status().isOk())
                .andExpect(view().name("dinnerHome/noGroup"));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    void shouldRenderRestaurantHistoryPageWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/restaurantHistory"))
                .andExpect(status().isOk())
                .andExpect(view().name("dinnerHome/restaurantHistory"));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    void shouldRenderMemberManagementPageWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/memberManagement"))
                .andExpect(status().isOk())
                .andExpect(view().name("dinnerHome/memberManagement"));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    void shouldRenderListRestaurantPageWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/listRestaurant"))
                .andExpect(status().isOk())
                .andExpect(view().name("dinnerHome/listRestaurant"));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    void shouldRenderCreateRestaurantPageWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dinnerHome/createRestaurant"))
                .andExpect(status().isOk())
                .andExpect(view().name("dinnerHome/createRestaurant"));
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
