package com.jan.web.security.utility;

import java.util.Date;

import com.jan.web.security.ValidationException;
import com.jan.web.security.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class JsonWebTokenUtility
{
    private static final Logger logger = LoggerFactory.getLogger(JsonWebTokenUtility.class);
    public static final String BEARER_ = "Bearer ";

    @Value("${jan.jwtSecret}")
    private String JWT_SECRET;

    @Value("${jan.jwtExpirationMs}")
    private int JWT_EXPIRATION_MS;

    public String generateJwtToken(User user)
    {
        return Jwts.builder()
                .setSubject((user.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String getUsernameFromJwtToken(String token)
    {
        if(token.contains(BEARER_))
        {
            token = token.substring(BEARER_.length());
        }
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken)
    {
        try
        {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (Exception e)
        {
            logger.error("Problem with JWT validation: {}", e.getMessage());
        }
        return false;
    }

    public String parseJwt(HttpServletRequest request)
    {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth))
        {
            if(!headerAuth.startsWith(BEARER_))
            {
                throw new ValidationException("There is no Bearer part of the HTTP header!");
            }
            return headerAuth.substring(BEARER_.length());
        }

        return null;
    }
}
