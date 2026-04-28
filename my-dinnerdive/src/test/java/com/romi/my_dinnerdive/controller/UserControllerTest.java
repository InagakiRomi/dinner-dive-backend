package com.romi.my_dinnerdive.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romi.my_dinnerdive.constant.UserCategory;
import com.romi.my_dinnerdive.dao.UserDao;
import com.romi.my_dinnerdive.dto.UserLoginRequest;
import com.romi.my_dinnerdive.dto.UserRegisterRequest;
import com.romi.my_dinnerdive.model.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
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

        User admin = userDao.getUserByUsername("testAdminRole01");
        assertNotNull(admin.getGroupId());
        assertNotEquals(1, admin.getGroupId());
        assertEquals("testAdminRole01的群組", userDao.getGroupNameByGroupId(admin.getGroupId()));
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
    @ParameterizedTest
    @ValueSource(strings = {"3#9$$^^", "abc-123", "name!"})
    public void shouldReturnBadRequestWhenRegisterUsernameHasInvalidFormat(String invalidUsername) throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername(invalidUsername);
        userRegisterRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    public void shouldReturnBadRequestWhenRegisterUsernameIsBlank(String blankUsername) throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername(blankUsername);
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

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    public void shouldReturnBadRequestWhenRegisterPasswordIsBlank(String blankPassword) throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("testBlankPwd01");
        userRegisterRequest.setUserPassword(blankPassword);

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

    @Test
    public void shouldReturnBadRequestWhenRegisterRolesQueryParamIsInvalid() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("testInvalidRole01");
        userRegisterRequest.setUserPassword("123");

        String json = objectMapper.writeValueAsString(userRegisterRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/register")
                        .param("roles", "INVALID_ROLE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenRegisterRequestBodyIsEmptyJson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username", notNullValue()))
                .andExpect(jsonPath("$.userPassword", notNullValue()));
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

    @ParameterizedTest
    @ValueSource(strings = {"hkbudsr324", "user-name", "john@"})
    public void shouldReturnBadRequestWhenLoginUsernameFormatIsInvalid(String invalidUsername) throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(invalidUsername);
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

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    public void shouldReturnBadRequestWhenLoginPasswordIsBlank(String blankPassword) throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername("John");
        userLoginRequest.setUserPassword(blankPassword);

        String json = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userPassword", notNullValue()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    public void shouldReturnBadRequestWhenLoginUsernameIsBlank(String blankUsername) throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(blankUsername);
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

    @Test
    public void shouldReturnBadRequestWhenLoginRequestBodyIsEmptyJson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username", notNullValue()))
                .andExpect(jsonPath("$.userPassword", notNullValue()));
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

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnCurrentGroupNameWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/group-name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName", equalTo("Test Team")));
    }

    @WithMockUser(username = "notExistsUser", roles = {"USER"})
    @Test
    public void shouldReturnUnauthorizedWhenGetCurrentGroupNameAndUserNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/group-name"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("使用者不存在")));
    }

    @Test
    public void shouldReturnUnauthorizedWhenGetCurrentGroupNameWithoutAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/group-name"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("尚未登入")));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnCurrentGroupMembersWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/group-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username", hasItem("super")))
                .andExpect(jsonPath("$[*].username", hasItem("user")));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Transactional
    @Test
    public void shouldUpdateCurrentGroupNameWhenAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/group-name")
                        .param("groupName", "Night Shift Team"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/group-name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName", equalTo("Night Shift Team")));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    public void shouldReturnForbiddenWhenNonAdminUpdatesGroupName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/group-name")
                        .param("groupName", "Not Allowed"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenAdminUpdatesGroupNameWithoutParam() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/group-name"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenAdminUpdatesGroupNameWithBlankValue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/group-name")
                        .param("groupName", "   "))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Transactional
    @Test
    public void shouldTransferAdminWhenTargetIsInSameGroup() throws Exception {
        Integer nextAdminUserId = userDao.getUserByUsername("user").getUserId();

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/transfer-admin")
                        .param("nextAdminUserId", String.valueOf(nextAdminUserId)))
                .andExpect(status().isOk());

        assertEquals(UserCategory.USER, userDao.getUserByUsername("super").getRoles());
        assertEquals(UserCategory.ADMIN, userDao.getUserByUsername("user").getRoles());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenTransferAdminTargetIsNotInSameGroup() throws Exception {
        Integer guestUserId = userDao.getUserByUsername("guest").getUserId();

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/transfer-admin")
                        .param("nextAdminUserId", String.valueOf(guestUserId)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    public void shouldReturnForbiddenWhenNonAdminTransfersAdmin() throws Exception {
        Integer nextAdminUserId = userDao.getUserByUsername("super").getUserId();

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/transfer-admin")
                        .param("nextAdminUserId", String.valueOf(nextAdminUserId)))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenTransferAdminTargetUserDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/transfer-admin")
                        .param("nextAdminUserId", "999999"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenTransferAdminWithoutTargetParam() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/transfer-admin"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Transactional
    @Test
    public void shouldDeleteCurrentGroupMemberWhenAdminDeletesSameGroupUser() throws Exception {
        Integer targetUserId = userDao.getUserByUsername("user").getUserId();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/group-members")
                        .param("targetUserId", String.valueOf(targetUserId)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/group-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username", hasItem("super")))
                .andExpect(jsonPath("$[*].username", not(hasItem("user"))));
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenAdminDeletesSelfFromGroupMembers() throws Exception {
        Integer currentUserId = userDao.getUserByUsername("super").getUserId();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/group-members")
                        .param("targetUserId", String.valueOf(currentUserId)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    public void shouldReturnForbiddenWhenNonAdminDeletesGroupMember() throws Exception {
        Integer targetUserId = userDao.getUserByUsername("super").getUserId();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/group-members")
                        .param("targetUserId", String.valueOf(targetUserId)))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenAdminDeletesMemberNotInSameGroup() throws Exception {
        Integer guestUserId = userDao.getUserByUsername("guest").getUserId();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/group-members")
                        .param("targetUserId", String.valueOf(guestUserId)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenAdminDeletesMemberThatDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/group-members")
                        .param("targetUserId", "999999"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "super", roles = {"ADMIN"})
    @Test
    public void shouldReturnBadRequestWhenDeleteGroupMemberWithoutTargetParam() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/group-members"))
                .andExpect(status().isBadRequest());
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