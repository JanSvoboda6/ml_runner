package com.jan.web.security.authentication;

import com.jan.web.security.role.Role;
import com.jan.web.security.role.RoleType;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserDetailsImpl;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class AuthorizationTokenFilterTest
{
    public static final String PASSWORD = "password";
    public static final String USERNAME = "user@email.com";
    public static final String RANDOM_JWT = "randomJWT";
    public static final String USER_NOT_FOUND = "User not found!";

    private JsonWebTokenUtility jsonWebTokenUtility;
    private UserDetailsService userDetailsService;
    private AuthorizationTokenFilter authorizationTokenFilter;

    @BeforeEach
    public void before()
    {
        jsonWebTokenUtility = Mockito.mock(JsonWebTokenUtility.class);
        userDetailsService = Mockito.mock(UserDetailsService.class);
        authorizationTokenFilter = new AuthorizationTokenFilter(jsonWebTokenUtility, userDetailsService);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @AfterEach
    public void after()
    {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void whenAuthorizationHeaderIsPresentAndJWTTokenIsPresent_thenJWTTokenIsValidated()
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(RANDOM_JWT);

        Assertions.assertThatThrownBy(() -> authorizationTokenFilter.doFilterInternal(request, response, filterChain))
                .hasMessage("Invalid JWT token supplied!");
    }

    @Test
    public void whenAuthorizationHeaderIsPresentAndJWTTokenIsNotPresent_thenUserDetailsAreNotLoaded() throws ServletException, IOException
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        authorizationTokenFilter.doFilterInternal(request, response, filterChain);

        Mockito.verifyZeroInteractions(userDetailsService);
    }

    @Test
    public void whenAuthorizationHeaderIsNotPresent_thenSecurityContextAuthenticationIsNotSet() throws ServletException, IOException
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        authorizationTokenFilter.doFilterInternal(request, response, filterChain);

        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void whenDoFilterInternalWithJwtIsFinished_thenNextFilterInTheChainIsCalled() throws ServletException, IOException
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(RANDOM_JWT);
        Mockito.when(jsonWebTokenUtility.validateJwtToken(RANDOM_JWT)).thenReturn(true);
        Mockito.when(jsonWebTokenUtility.getUsernameFromJwtToken(RANDOM_JWT)).thenReturn(USERNAME);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        User user = createArtificialUser();
        user.setRoles(Set.of(new Role(RoleType.ROLE_USER)));
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Mockito.when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        authorizationTokenFilter.doFilterInternal(request, response, filterChain);

        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenDoFilterInternalWithoutJwtIsFinished_thenNextFilterInTheChainIsCalled() throws ServletException, IOException
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(null);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        authorizationTokenFilter.doFilterInternal(request, response, filterChain);

        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenJWTTokenIsSuccessfullyValidatedAndUserIsFound_thenSecurityContextAuthenticationIsSet() throws ServletException, IOException
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(RANDOM_JWT);
        Mockito.when(jsonWebTokenUtility.validateJwtToken(RANDOM_JWT)).thenReturn(true);

        Mockito.when(jsonWebTokenUtility.getUsernameFromJwtToken(RANDOM_JWT)).thenReturn(USERNAME);
        User user = createArtificialUser();

        user.setRoles(Set.of(new Role(RoleType.ROLE_USER)));
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        Mockito.when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        authorizationTokenFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    public void whenJWTTokenIsSuccessfullyValidatedAndUserIsNotFound_thenSecurityContextIsNotSet()
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(RANDOM_JWT);
        Mockito.when(jsonWebTokenUtility.validateJwtToken(RANDOM_JWT)).thenReturn(true);

        Mockito.when(jsonWebTokenUtility.getUsernameFromJwtToken(RANDOM_JWT)).thenReturn(USERNAME);
        Mockito.when(userDetailsService.loadUserByUsername(USERNAME)).thenThrow(new UsernameNotFoundException(USER_NOT_FOUND));
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        Assertions.assertThatThrownBy(() -> authorizationTokenFilter.doFilterInternal(request, response, filterChain))
                .hasMessage(USER_NOT_FOUND);

        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void whenJWTTokenValidationFails_thenSecurityContextIsNotSet()
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(RANDOM_JWT);
        Mockito.when(jsonWebTokenUtility.validateJwtToken(RANDOM_JWT)).thenReturn(false);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        Assertions.assertThatThrownBy(() -> authorizationTokenFilter.doFilterInternal(request, response, filterChain))
                .hasMessage("Invalid JWT token supplied!");

        if(SecurityContextHolder.getContext().getAuthentication() != null)
        {
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
        }
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private User createArtificialUser()
    {
        User user = new User(USERNAME, PASSWORD);
        user.setRoles(Set.of(new Role(RoleType.ROLE_USER)));
        return user;
    }
}