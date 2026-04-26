package com.romi.my_dinnerdive.controller;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class RestaurantHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnHistoriesWithDefaultParamsWhenAuthenticated() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurantHistories");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$.total", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.results", notNullValue()));
    }

    @Test
    void shouldRedirectToLoginWhenGetHistoriesWithoutAuthentication() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurantHistories");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldContainLatestRecordWhenQueryAfterChoose() throws Exception {
        RequestBuilder chooseRequest = MockMvcRequestBuilders
                .patch("/choose/{restaurantId}", 1);

        mockMvc.perform(chooseRequest)
                .andExpect(status().isOk());

        RequestBuilder historyRequest = MockMvcRequestBuilders
                .get("/restaurantHistories")
                .param("orderBy", "selected_at")
                .param("sort", "DESC")
                .param("limit", "1")
                .param("offset", "0");

        mockMvc.perform(historyRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.results", hasSize(1)))
                .andExpect(jsonPath("$.results[0].restaurantId").value(1))
                .andExpect(jsonPath("$.results[0].restaurantName").value("一番湯屋"))
                .andExpect(jsonPath("$.results[0].category").value("主食"))
                .andExpect(jsonPath("$.results[0].selectedAt", notNullValue()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnHistoriesWhenUsingPagingParams() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurantHistories")
                .param("orderBy", "restaurant_name")
                .param("sort", "ASC")
                .param("limit", "2")
                .param("offset", "0");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(2))
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$.results", notNullValue()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldReturnEmptyResultsWhenLimitIsZero() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurantHistories")
                .param("limit", "0");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(0))
                .andExpect(jsonPath("$.results", hasSize(0)));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldThrowValidationWhenLimitExceedsMax() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurantHistories")
                .param("limit", "1001");

        Exception ex = assertThrows(ServletException.class, () -> mockMvc.perform(requestBuilder));
        assertTrue(ex.getCause() instanceof ConstraintViolationException);
        assertTrue(ex.getCause().getMessage().contains("getRestaurantHistories.limit"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldThrowValidationWhenOffsetIsNegative() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurantHistories")
                .param("offset", "-1");

        Exception ex = assertThrows(ServletException.class, () -> mockMvc.perform(requestBuilder));
        assertTrue(ex.getCause() instanceof ConstraintViolationException);
        assertTrue(ex.getCause().getMessage().contains("getRestaurantHistories.offset"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void shouldThrowValidationWhenLimitIsNegative() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/restaurantHistories")
                .param("limit", "-1");

        Exception ex = assertThrows(ServletException.class, () -> mockMvc.perform(requestBuilder));
        assertTrue(ex.getCause() instanceof ConstraintViolationException);
        assertTrue(ex.getCause().getMessage().contains("getRestaurantHistories.limit"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    @Test
    void shouldThrowValidationWhenQueryHistoryWithInvalidOffsetAfterChoose() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/choose/{restaurantId}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(2));

        RequestBuilder invalidHistoryQuery = MockMvcRequestBuilders
                .get("/restaurantHistories")
                .param("orderBy", "selected_at")
                .param("sort", "DESC")
                .param("limit", "10")
                .param("offset", "-5");

        Exception ex = assertThrows(ServletException.class, () -> mockMvc.perform(invalidHistoryQuery));
        assertTrue(ex.getCause() instanceof ConstraintViolationException);
        assertTrue(ex.getCause().getMessage().contains("getRestaurantHistories.offset"));
    }
}
