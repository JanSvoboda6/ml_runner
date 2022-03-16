package com.jan.web.security.authentication;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(SpringRunner.class)
public class AuthenticationEntryPointJwtTest
{
    @Test
    public void whenCommenceMethodIsCalled_thenErrorWithUnauthorizedMessageIsSent() throws IOException
    {
        AuthenticationEntryPointJwt entryPointJwt = new AuthenticationEntryPointJwt();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthenticationException authException = Mockito.mock(AuthenticationException.class);
        entryPointJwt.commence(request, response, authException);
        Mockito.verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}