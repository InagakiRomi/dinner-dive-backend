package com.romi.my_dinnerdive.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romi.my_dinnerdive.constant.UserCategory;
import com.romi.my_dinnerdive.dao.UserDao;
import com.romi.my_dinnerdive.dto.UserLoginRequest;
import com.romi.my_dinnerdive.dto.UserRegisterRequest;
import com.romi.my_dinnerdive.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:usertestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
@SuppressWarnings("null")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    // ===== 註冊成功案例 =====
    @Test
    public void shouldRegisterUserWhenRequestIsValid() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("test1");
        userRegisterRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.username", equalTo("test1")))
                .andExpect(jsonPath("$.roles", equalTo("USER")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));

        // 檢查資料庫中的密碼不為明碼
        User user = userDao.getUserByUsername(userRegisterRequest.getUsername());
        assertNotEquals(userRegisterRequest.getUserPassword(), user.getUserPassword());
    }

    @Test
    public void shouldRegisterUserWithExplicitRoleFromBody() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("testAdminRole01");
        userRegisterRequest.setUserPassword("123");
        userRegisterRequest.setRoles(UserCategory.ADMIN);

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.username", equalTo("testAdminRole01")))
                .andExpect(jsonPath("$.roles", equalTo("ADMIN")));
    }

    @Test
    public void shouldRegisterUserWithRoleFromQueryParamWhenBodyRoleMissing() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("testGuestRole01");
        userRegisterRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .param("roles", "GUEST")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.username", equalTo("testGuestRole01")))
                .andExpect(jsonPath("$.roles", equalTo("GUEST")));
    }

    @Test
    public void shouldUseBodyRoleWhenBodyAndQueryRolesBothProvided() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("testRolePriority01");
        userRegisterRequest.setUserPassword("123");
        userRegisterRequest.setRoles(UserCategory.ADMIN);

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .param("roles", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.roles", equalTo("ADMIN")));
    }

    // ===== 註冊失敗/驗證案例 =====
    @Test
    public void shouldReturnBadRequestWhenRegisterUsernameHasInvalidFormat() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("3#9$$^^");
        userRegisterRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturnBadRequestWhenRegisterUsernameIsBlank() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername(" ");
        userRegisterRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username", notNullValue()));
    }

    @Test
    public void shouldReturnConflictWhenRegisterUsernameAlreadyExists() throws Exception {
        // 先註冊一個帳號
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("test2");
        userRegisterRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));

        // 再次使用同個帳號註冊
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(409));
    }

    @Test
    public void shouldReturnBadRequestWhenRegisterPasswordIsBlank() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("testBlankPwd01");
        userRegisterRequest.setUserPassword("");

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userPassword", notNullValue()));
    }

    @Test
    public void shouldReturnBadRequestWhenRegisterWithMalformedJson() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"broken\"");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    // ===== 登入成功案例 =====
    @Test
    public void shouldLoginWhenCredentialsAreValid() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("test3");
        userRegisterRequest.setUserPassword("123");

        register(userRegisterRequest);

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(userRegisterRequest.getUsername());
        userLoginRequest.setUserPassword(userRegisterRequest.getUserPassword());

        String json = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.username", equalTo(userRegisterRequest.getUsername())))
                .andExpect(jsonPath("$.roles", equalTo("USER")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    // ===== 登入失敗/驗證案例 =====
    @Test
    public void shouldReturnBadRequestWhenLoginPasswordIsWrong() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("test4");
        userRegisterRequest.setUserPassword("123");

        register(userRegisterRequest);

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(userRegisterRequest.getUsername());
        userLoginRequest.setUserPassword("unknown");

        String json = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturnBadRequestWhenLoginUsernameFormatIsInvalid() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername("hkbudsr324");
        userLoginRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturnBadRequestWhenLoginUsernameDoesNotExist() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername("unknown");
        userLoginRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturnBadRequestWhenLoginPasswordIsBlank() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername("John");
        userLoginRequest.setUserPassword("");

        String json = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userPassword", notNullValue()));
    }

    @Test
    public void shouldReturnBadRequestWhenLoginUsernameIsBlank() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername("");
        userLoginRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username", notNullValue()));
    }

    @Test
    public void shouldReturnBadRequestWhenLoginWithMalformedJson() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"broken\"");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    // ===== 使用者流程案例 =====
    @Test
    public void shouldCompleteRegisterThenLoginFlow() throws Exception {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("flowUser01");
        registerRequest.setUserPassword("123");

        String registerJson = objectMapper.writeValueAsString(registerRequest);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", equalTo("flowUser01")))
                .andExpect(jsonPath("$.roles", equalTo("USER")));

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("flowUser01");
        loginRequest.setUserPassword("123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo("flowUser01")))
                .andExpect(jsonPath("$.roles", equalTo("USER")))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    @Test
    public void shouldReturnBadRequestThenConflictInNegativeRegisterLoginFlow() throws Exception {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("flowUser02");
        registerRequest.setUserPassword("123");

        String registerJson = objectMapper.writeValueAsString(registerRequest);
        RequestBuilder registerBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson);

        mockMvc.perform(registerBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", equalTo("flowUser02")));

        UserLoginRequest wrongLogin = new UserLoginRequest();
        wrongLogin.setUsername("flowUser02");
        wrongLogin.setUserPassword("wrong-password");

        String wrongLoginJson = objectMapper.writeValueAsString(wrongLogin);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongLoginJson))
                .andExpect(status().isBadRequest());

        mockMvc.perform(registerBuilder)
                .andExpect(status().isConflict());
    }

    private void register(UserRegisterRequest userRegisterRequest) throws Exception {
        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));
    }
}