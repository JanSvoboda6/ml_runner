package com.jan.web.security.authentication;

import com.jan.web.security.validation.ValidationException;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Class for validating the JWT if Authorization header is present.
 * It sets context in {@link SecurityContextHolder} so that Spring Security
 * doesn't block the request (if JWT is valid).
 */
public class AuthorizationTokenFilter extends OncePerRequestFilter
{
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationTokenFilter.class);
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserRepository userRepository;

    @Autowired
    public AuthorizationTokenFilter(JsonWebTokenUtility jsonWebTokenUtility, UserRepository userRepository)
    {
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userRepository = userRepository;
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
            Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(jwt));
            if(user.isEmpty())
            {
                throw new ValidationException("No user found from supplied JWT token!");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.get().getUsername(), null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
