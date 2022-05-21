package com.jan.web.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Provides a handling of an exception during http filtering in {@link com.jan.web.security.configuration.WebSecurityConfig}.
 */
@Component
public class AuthenticationEntryPointJwt implements AuthenticationEntryPoint
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException
    {
        LOGGER.error("Unauthorized error: {}", authenticationException.getMessage());
        response.setContentType("application/json");
        response.getOutputStream().println("{ \"message\":\"Authorization error! Looks like your access token has expired. Please sign in again.\" }");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
