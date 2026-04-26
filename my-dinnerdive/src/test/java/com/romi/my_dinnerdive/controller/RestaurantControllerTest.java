package com.romi.my_dinnerdive.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.romi.my_dinnerdive.constant.RestaurantCategory;
import com.romi.my_dinnerdive.dto.RestaurantRequest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnRestaurantWhenIdExists() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/restaurants/{restaurantId}", 1);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantName", equalTo("一番湯屋")))
                .andExpect(jsonPath("$.category", equalTo("MAIN")))
                .andExpect(jsonPath("$.imageUrl", notNullValue()))
                .andExpect(jsonPath("$.visitedCount", notNullValue()))
                .andExpect(jsonPath("$.lastSelectedAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$.note", notNullValue()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    public void shouldReturnNotFoundWhenRestaurantIdDoesNotExist() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants/{restaurants}", 20000);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    @Test
    void shouldRedirectToLoginWhenGetRestaurantWithoutAuthentication() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants/{restaurantId}", 1);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldCreateRestaurantWhenRequestIsValid() throws Exception{
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setRestaurantName("都不NONO");
        restaurantRequest.setCategory(RestaurantCategory.DRINK);
        restaurantRequest.setImageUrl("http://test.com");
        restaurantRequest.setNote("布丁五姊妹好喝");

        String json = objectMapper.writeValueAsString(restaurantRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.restaurantName", equalTo("都不NONO")))
                .andExpect(jsonPath("$.category", equalTo("DRINK")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.visitedCount", equalTo(0)))
	        .andExpect(jsonPath("$.lastSelectedAt", nullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$.note", equalTo("布丁五姊妹好喝")));
    }

    // 修改餐廳
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldUpdateRestaurantWhenRestaurantExists() throws Exception{
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setRestaurantName("好棒棒喔");
        restaurantRequest.setCategory(RestaurantCategory.SNACK);
        restaurantRequest.setImageUrl("http://test.food");
        restaurantRequest.setVisitedCount(6);
        restaurantRequest.setNote("肌肉猛男開的專賣店");

        String json = objectMapper.writeValueAsString(restaurantRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/restaurants/{restaurantId}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.restaurantName", equalTo("好棒棒喔")))
                .andExpect(jsonPath("$.category", equalTo("SNACK")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.food")))
                .andExpect(jsonPath("$.visitedCount", equalTo(6)))
	        .andExpect(jsonPath("$.lastSelectedAt", nullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$.note", equalTo("肌肉猛男開的專賣店")));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    public void shouldReturnNotFoundWhenUpdateRestaurantDoesNotExist() throws Exception {
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setRestaurantName("好棒棒喔");
        restaurantRequest.setCategory(RestaurantCategory.SNACK);
        restaurantRequest.setImageUrl("http://test.food");
        restaurantRequest.setVisitedCount(6);
        restaurantRequest.setNote("肌肉猛男開的專賣店");

        String json = objectMapper.writeValueAsString(restaurantRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/restaurants/{restaurantId}", 2000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    // 刪除餐廳
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    public void shouldDeleteRestaurantWhenAdminDeletesExistingRestaurant() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/restaurants/{restaurantId}", 5);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(204));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    public void shouldReturnNoContentWhenAdminDeletesNonExistingRestaurant() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/restaurants/{restaurantId}", 20000);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(204));
    }

    // 查詢餐廳列表
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    public void shouldReturnRestaurantPageWhenQueryWithoutFilters() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(10)));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    public void shouldReturnFilteredRestaurantsWhenSearchAndCategoryProvided() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants")
                .param("search", "奶")
                .param("category", "DRINK");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(3)));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    public void shouldReturnSortedRestaurantsWhenOrderByAndSortProvided() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants")
                .param("orderBy", "restaurant_id")
                .param("sort", "asc");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(10)))
                .andExpect(jsonPath("$.results[0].restaurantId", equalTo(1)))
                .andExpect(jsonPath("$.results[1].restaurantId", equalTo(2)))
                .andExpect(jsonPath("$.results[2].restaurantId", equalTo(3)))
                .andExpect(jsonPath("$.results[3].restaurantId", equalTo(4)))
                .andExpect(jsonPath("$.results[4].restaurantId", equalTo(5)));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    public void shouldReturnPagedRestaurantsWhenLimitAndOffsetProvided() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants")
                .param("limit", "3")
                .param("offset", "4");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(3)))
                .andExpect(jsonPath("$.results[0].restaurantId", equalTo(5)))
                .andExpect(jsonPath("$.results[1].restaurantId", equalTo(6)));
    }

    // 選擇餐廳
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldChooseRestaurantWhenRestaurantExists() throws Exception{
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setRestaurantName("乾乾拌拌");
        restaurantRequest.setCategory(RestaurantCategory.MAIN);
        restaurantRequest.setImageUrl("https://cdn.pixabay.com/photo/2020/03/31/01/56/fried-rice-4985989_1280.jpg");
        restaurantRequest.setVisitedCount(0);
        restaurantRequest.setNote("服務態度比飯好");

        String json = objectMapper.writeValueAsString(restaurantRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/choose/{restaurantId}", 22)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.restaurantName", equalTo("乾乾拌拌")))
                .andExpect(jsonPath("$.category", equalTo("MAIN")))
                .andExpect(jsonPath("$.imageUrl", equalTo("https://cdn.pixabay.com/photo/2020/03/31/01/56/fried-rice-4985989_1280.jpg")))
                .andExpect(jsonPath("$.visitedCount", equalTo(1)))
	        .andExpect(jsonPath("$.lastSelectedAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$.note", equalTo("服務態度比飯好")));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldReturnHistoriesAfterChooseRestaurant() throws Exception {
        RequestBuilder chooseRequest = MockMvcRequestBuilders
                .patch("/choose/{restaurantId}", 1)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(chooseRequest)
                .andExpect(status().is(200));

        RequestBuilder historyRequest = MockMvcRequestBuilders
                .get("/restaurantHistories")
                .param("orderBy", "selected_at")
                .param("sort", "DESC");

        mockMvc.perform(historyRequest)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.total", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.results[0].restaurantId", equalTo(1)))
                .andExpect(jsonPath("$.results[0].restaurantName", equalTo("一番湯屋")))
                .andExpect(jsonPath("$.results[0].selectedAt", notNullValue()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldThrowValidationWhenGetRestaurantsWithNegativeLimit() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurants")
                .param("limit", "-1");

        Exception ex = assertThrows(ServletException.class, () -> mockMvc.perform(requestBuilder));
        assertTrue(ex.getCause() instanceof ConstraintViolationException);
        assertTrue(ex.getCause().getMessage().contains("getRestaurants.limit"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnBadRequestWhenCreateRestaurantWithoutName() throws Exception {
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setCategory(RestaurantCategory.MAIN);
        restaurantRequest.setImageUrl("http://test.com/no-name");
        restaurantRequest.setNote("缺少店名測試");

        String json = objectMapper.writeValueAsString(restaurantRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.restaurantName", notNullValue()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnBadRequestWhenCreateRestaurantWithoutCategory() throws Exception {
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setRestaurantName("缺分類餐廳");
        restaurantRequest.setImageUrl("http://test.com/no-category");

        String json = objectMapper.writeValueAsString(restaurantRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.category", notNullValue()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnBadRequestWhenCreateRestaurantWithMalformedJson() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"restaurantName\":\"壞掉資料\"");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnRandomRestaurantWhenCategoryIsValid() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/random")
                .param("category", "DRINK");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId", notNullValue()))
                .andExpect(jsonPath("$.restaurantName", notNullValue()))
                .andExpect(jsonPath("$.category", equalTo("飲料")))
                .andExpect(jsonPath("$.visitedCount", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnBadRequestWhenRandomCategoryIsInvalid() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/random")
                .param("category", "INVALID");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldClearRandomPoolWhenAdminCallsClearRandom() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/clearRandom");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnNotFoundWhenChooseRestaurantDoesNotExist() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/choose/{restaurantId}", 99999);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "member", roles = {"USER"})
    @Test
    void shouldReturnForbiddenWhenDeleteRestaurantWithoutAdminRole() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/restaurants/{restaurantId}", 1);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldCompleteRandomChooseHistoryClearFlow() throws Exception {
        String randomResponse = mockMvc.perform(
                        MockMvcRequestBuilders.get("/random")
                                .param("category", "MAIN"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode randomJson = objectMapper.readTree(randomResponse);
        int restaurantId = randomJson.get("restaurantId").asInt();

        mockMvc.perform(MockMvcRequestBuilders.patch("/choose/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId", equalTo(restaurantId)))
                .andExpect(jsonPath("$.lastSelectedAt", notNullValue()));

        mockMvc.perform(MockMvcRequestBuilders.get("/restaurantHistories")
                        .param("orderBy", "selected_at")
                        .param("sort", "DESC")
                        .param("limit", "1")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(1)))
                .andExpect(jsonPath("$.results[0].restaurantId", equalTo(restaurantId)))
                .andExpect(jsonPath("$.results[0].selectedAt", notNullValue()));

        mockMvc.perform(MockMvcRequestBuilders.post("/clearRandom"))
                .andExpect(status().isOk());
    }
}
