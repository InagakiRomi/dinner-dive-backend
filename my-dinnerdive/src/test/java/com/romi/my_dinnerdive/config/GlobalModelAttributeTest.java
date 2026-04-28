package com.romi.my_dinnerdive.config;

import java.security.Principal;
import java.util.List;
import java.util.stream.Stream;

import com.romi.my_dinnerdive.constant.UserCategory;
import com.romi.my_dinnerdive.dao.UserDao;
import com.romi.my_dinnerdive.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalModelAttributeTest {

    private static Stream<Arguments> roleCases() {
        return Stream.of(
                Arguments.of("member", UserCategory.USER, "ROLE_USER", "一般使用者", false),
                Arguments.of("super", UserCategory.ADMIN, "ROLE_ADMIN", "管理員", true)
        );
    }

    private GlobalModelAttribute buildAdvice(UserDao userDao) {
        GlobalModelAttribute advice = new GlobalModelAttribute();
        ReflectionTestUtils.setField(advice, "userDao", userDao);
        return advice;
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetRestrictedViewFalseAndSkipUserInfoWhenPrincipalIsNull() {
        UserDao userDao = mock(UserDao.class);
        GlobalModelAttribute advice = buildAdvice(userDao);
        Model model = new ExtendedModelMap();

        advice.addUserInfoToModel(model, null);

        assertEquals(false, model.getAttribute("restrictedView"));
        assertNull(model.getAttribute("username"));
        assertNull(model.getAttribute("roles"));
        assertNull(model.getAttribute("groupName"));
    }

    @Test
    void shouldSetRestrictedMessageWhenUserHasNoGroupName() {
        UserDao userDao = mock(UserDao.class);
        GlobalModelAttribute advice = buildAdvice(userDao);
        Model model = new ExtendedModelMap();

        User user = new User();
        user.setUserId(1);
        user.setUsername("member");
        user.setGroupId(99);
        user.setRoles(UserCategory.USER);

        when(userDao.getUserByUsername("member")).thenReturn(user);
        when(userDao.getGroupNameByGroupId(99)).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "member",
                        "N/A",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        Principal principal = () -> "member";
        advice.addUserInfoToModel(model, principal);

        assertEquals(true, model.getAttribute("restrictedView"));
        assertEquals("還沒有加入任何群組哦請尋找管理員加入群組", model.getAttribute("restrictedMessage"));
        assertEquals("member", model.getAttribute("username"));
        assertEquals("一般使用者", model.getAttribute("roles"));
        assertEquals("", model.getAttribute("groupName"));
        assertEquals(false, model.getAttribute("isAdmin"));
    }

    @ParameterizedTest
    @MethodSource("roleCases")
    void shouldSetUserRoleDisplayNameAndGroupInfoWhenPrincipalExists(
            String username,
            UserCategory role,
            String authority,
            String expectedRoleDisplay,
            boolean expectedIsAdmin) {
        UserDao userDao = mock(UserDao.class);
        GlobalModelAttribute advice = buildAdvice(userDao);
        Model model = new ExtendedModelMap();

        User user = new User();
        user.setUserId(2);
        user.setUsername(username);
        user.setGroupId(1);
        user.setRoles(role);

        when(userDao.getUserByUsername(username)).thenReturn(user);
        when(userDao.getGroupNameByGroupId(1)).thenReturn("Test Team");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        "N/A",
                        List.of(
                                new SimpleGrantedAuthority(authority),
                                new SimpleGrantedAuthority("ROLE_UNKNOWN"))));

        Principal principal = () -> username;
        advice.addUserInfoToModel(model, principal);

        assertEquals(false, model.getAttribute("restrictedView"));
        assertEquals(username, model.getAttribute("username"));
        assertEquals(expectedRoleDisplay, model.getAttribute("roles"));
        assertEquals("Test Team", model.getAttribute("groupName"));
        assertEquals(expectedIsAdmin, model.getAttribute("isAdmin"));
    }
}
