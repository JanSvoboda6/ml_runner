package com.jan.web.security.authentication;

import com.jan.web.security.utility.JsonWebTokenUtility;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationTokenFilterTest
{
    public static final String PASSWORD = "password";
    public static final String USERNAME = "user@email.com";
    public static final String RANDOM_JWT = "randomJWT";
    public static final String USER_NOT_FOUND = "User not found!";

    private JsonWebTokenUtility jsonWebTokenUtility;
    private AuthorizationTokenFilter authorizationTokenFilter;

    @BeforeEach
    public void before()
    {
        jsonWebTokenUtility = Mockito.mock(JsonWebTokenUtility.class);
        authorizationTokenFilter = new AuthorizationTokenFilter(jsonWebTokenUtility);
    }

    @Test
    public void whenAuthorizationHeaderIsPresentAndJWTTokenIsPresent_thenJWTTokenIsValidated()
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(RANDOM_JWT);
        Mockito.when(jsonWebTokenUtility.validateJwtToken(RANDOM_JWT)).thenReturn(true);

        Assertions.assertThatCode(() -> authorizationTokenFilter.doFilterInternal(request, response, filterChain))
                .doesNotThrowAnyException();
    }

    @Test
    public void whenAuthorizationHeaderIsNotPresent_thenSecurityContextAuthenticationIsNotSet() throws ServletException, IOException
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(null);

        authorizationTokenFilter.doFilterInternal(request, response, filterChain);

        Mockito.verify(jsonWebTokenUtility, Mockito.times(0)).validateJwtToken(Mockito.anyString());
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
    public void whenJWTTokenValidationFails_thenExceptionIsThrown()
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(jsonWebTokenUtility.parseJwt(request)).thenReturn(RANDOM_JWT);
        Mockito.when(jsonWebTokenUtility.validateJwtToken(RANDOM_JWT)).thenReturn(false);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        Assertions.assertThatThrownBy(() -> authorizationTokenFilter.doFilterInternal(request, response, filterChain))
                .hasMessage("Invalid JWT token supplied!");
    }
}