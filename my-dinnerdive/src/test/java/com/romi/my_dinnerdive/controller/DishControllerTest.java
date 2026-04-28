package com.romi.my_dinnerdive.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romi.my_dinnerdive.dto.DishRequest;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockHttpServletRequestBuilder createDishRequest(DishRequest dishRequest) throws Exception {
        return MockMvcRequestBuilders
                .post("/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dishRequest));
    }

    // ===== 查詢成功案例 =====
    @WithMockUser(username = "member", roles = {"USER"})
    @Test
    void shouldReturnDishesWhenRestaurantExists() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants/{restaurantId}/dishes", 1);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].dishId", equalTo(1)))
                .andExpect(jsonPath("$[0].restaurantId", equalTo(1)))
                .andExpect(jsonPath("$[0].dishName", equalTo("豚骨拉麵")))
                .andExpect(jsonPath("$[0].price", equalTo(280)));
    }

    @WithMockUser(username = "member", roles = {"USER"})
    @Test
    void shouldReturnEmptyListWhenRestaurantDoesNotExist() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants/{restaurantId}/dishes", 20000);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===== 新增成功案例 =====
    @WithMockUser(username = "member", roles = {"USER"})
    @Transactional
    @Test
    void shouldCreateDishWhenRequestIsValid() throws Exception {
        DishRequest dishRequest = new DishRequest();
        dishRequest.setRestaurantId(1);
        dishRequest.setDishName("測試椒麻雞");
        dishRequest.setPrice(199);

        mockMvc.perform(createDishRequest(dishRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dishId", notNullValue()))
                .andExpect(jsonPath("$.restaurantId", equalTo(1)))
                .andExpect(jsonPath("$.dishName", equalTo("測試椒麻雞")))
                .andExpect(jsonPath("$.price", equalTo(199)));
    }

    // ===== 新增失敗/驗證案例 =====
    @WithMockUser(username = "member", roles = {"USER"})
    @ParameterizedTest
    @CsvSource({
            "WITHOUT_DISH_NAME,dishName",
            "WITHOUT_RESTAURANT_ID,restaurantId",
            "WITHOUT_PRICE,price",
            "BLANK_DISH_NAME,dishName"
    })
    void shouldReturnBadRequestWhenCreateDishRequestInvalid(String scenario, String expectedField) throws Exception {
        DishRequest dishRequest = new DishRequest();
        switch (scenario) {
            case "WITHOUT_DISH_NAME":
                dishRequest.setRestaurantId(1);
                dishRequest.setPrice(120);
                break;
            case "WITHOUT_RESTAURANT_ID":
                dishRequest.setDishName("測試缺餐廳");
                dishRequest.setPrice(120);
                break;
            case "WITHOUT_PRICE":
                dishRequest.setRestaurantId(1);
                dishRequest.setDishName("測試缺價格");
                break;
            case "BLANK_DISH_NAME":
                dishRequest.setRestaurantId(1);
                dishRequest.setPrice(100);
                dishRequest.setDishName("  ");
                break;
            default:
                throw new IllegalArgumentException("Unsupported scenario: " + scenario);
        }

        mockMvc.perform(createDishRequest(dishRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + expectedField, notNullValue()));
    }

    @WithMockUser(username = "member", roles = {"USER"})
    @Test
    void shouldReturnBadRequestWhenCreateDishWithMalformedJson() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"restaurantId\":1,\"dishName\":\"壞掉資料\"");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "member", roles = {"USER"})
    @Test
    void shouldReturnNotFoundWhenCreateDishWithNonExistingRestaurant() throws Exception {
        DishRequest dishRequest = new DishRequest();
        dishRequest.setRestaurantId(999999);
        dishRequest.setDishName("不存在餐廳餐點");
        dishRequest.setPrice(120);

        mockMvc.perform(createDishRequest(dishRequest))
                .andExpect(status().isNotFound());
    }

    // ===== 刪除成功案例 =====
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldDeleteDishWhenAdminDeletesExistingDish() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/dishes/{dishId}", 1);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldReturnNoContentWhenAdminDeletesNonExistingDish() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/dishes/{dishId}", 99999);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    // ===== 權限/登入案例 =====
    @WithMockUser(username = "member", roles = {"USER"})
    @Test
    void shouldReturnForbiddenWhenUserDeletesDishWithoutAdminRole() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/dishes/{dishId}", 1);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({
            "DELETE,/dishes/{dishId},1",
            "GET,/restaurants/{restaurantId}/dishes,1"
    })
    void shouldRedirectToLoginWhenAccessWithoutAuthentication(String method, String path, int id) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = "DELETE".equals(method)
                ? MockMvcRequestBuilders.delete(path, id)
                : MockMvcRequestBuilders.get(path, id);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection());
    }
}
