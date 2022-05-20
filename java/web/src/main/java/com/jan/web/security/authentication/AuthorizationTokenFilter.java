package com.jan.web.security.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jan.web.security.ValidationException;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthorizationTokenFilter extends OncePerRequestFilter
{
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationTokenFilter.class);
    private final JsonWebTokenUtility jsonWebTokenUtility;

    @Autowired
    public AuthorizationTokenFilter(JsonWebTokenUtility jsonWebTokenUtility)
    {
        this.jsonWebTokenUtility = jsonWebTokenUtility;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, ValidationException
    {
        String jwt = jsonWebTokenUtility.parseJwt(request);
        if (jwt != null)
        {
            if (!jsonWebTokenUtility.validateJwtToken(jwt))
            {
                ValidationException validationException = new ValidationException("Invalid JWT token supplied!");
                logger.error(validationException.getMessage());
                throw validationException;
            }
        }
        filterChain.doFilter(request, response);
    }
}
