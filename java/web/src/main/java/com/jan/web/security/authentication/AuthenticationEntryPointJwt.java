package com.jan.web.security.authentication;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPointJwt implements AuthenticationEntryPoint
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException
    {
        logger.error("Unauthorized error: {}", authenticationException.getMessage());
        response.setContentType("application/json");
        response.getOutputStream().println("{ \"message\":\"Authorization error! Looks like your access token has expired. Please sign in again.\" }");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
