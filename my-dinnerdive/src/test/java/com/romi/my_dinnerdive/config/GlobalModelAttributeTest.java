package com.romi.my_dinnerdive.config;

import java.security.Principal;
import java.util.List;

import com.romi.my_dinnerdive.constant.UserCategory;
import com.romi.my_dinnerdive.dao.UserDao;
import com.romi.my_dinnerdive.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetRestrictedViewFalseAndSkipUserInfoWhenPrincipalIsNull() {
        GlobalModelAttribute advice = new GlobalModelAttribute();
        UserDao userDao = mock(UserDao.class);
        ReflectionTestUtils.setField(advice, "userDao", userDao);
        Model model = new ExtendedModelMap();

        advice.addUserInfoToModel(model, null);

        assertEquals(false, model.getAttribute("restrictedView"));
        assertNull(model.getAttribute("username"));
        assertNull(model.getAttribute("roles"));
        assertNull(model.getAttribute("groupName"));
    }

    @Test
    void shouldSetRestrictedMessageWhenUserHasNoGroupName() {
        GlobalModelAttribute advice = new GlobalModelAttribute();
        UserDao userDao = mock(UserDao.class);
        ReflectionTestUtils.setField(advice, "userDao", userDao);
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

    @Test
    void shouldSetUserRoleDisplayNameAndGroupInfoWhenPrincipalExists() {
        GlobalModelAttribute advice = new GlobalModelAttribute();
        UserDao userDao = mock(UserDao.class);
        ReflectionTestUtils.setField(advice, "userDao", userDao);
        Model model = new ExtendedModelMap();

        User user = new User();
        user.setUserId(2);
        user.setUsername("super");
        user.setGroupId(1);
        user.setRoles(UserCategory.ADMIN);

        when(userDao.getUserByUsername("super")).thenReturn(user);
        when(userDao.getGroupNameByGroupId(1)).thenReturn("Test Team");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "super",
                        "N/A",
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN"),
                                new SimpleGrantedAuthority("ROLE_UNKNOWN"))));

        Principal principal = () -> "super";
        advice.addUserInfoToModel(model, principal);

        assertEquals(false, model.getAttribute("restrictedView"));
        assertEquals("super", model.getAttribute("username"));
        assertEquals("管理員", model.getAttribute("roles"));
        assertEquals("Test Team", model.getAttribute("groupName"));
        assertEquals(true, model.getAttribute("isAdmin"));
    }
}
