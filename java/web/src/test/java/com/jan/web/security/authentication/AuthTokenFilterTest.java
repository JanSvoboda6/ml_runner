package com.jan.web.security.authentication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AuthTokenFilterTest
{

    @Test
    public void whenAuthorizationHeaderIsPresentAndJWTTokenIsPresent_thenJWTTokenIsParsed()
    {
        Assertions.fail();
    }

    @Test
    public void whenAuthorizationHeaderIsPresentAndJWTTokenIsNotPresent_thenExceptionIsThrown()
    {
        Assertions.fail();
    }

    @Test
    public void whenAuthorizationHeaderIsNotPresent_thenSecurityContextIsSet()
    {
        Assertions.fail();
    }

    @Test
    public void whenDoFilterInternalIsFinished_thenNextFilterInTheChainIsCalled()
    {
        Assertions.fail();
    }

    @Test
    public void whenJWTTokenIsSuccessfullyValidatedAndUserIsFound_thenSecurityContextIsSet()
    {
        Assertions.fail();
    }

    @Test
    public void whenJWTTokenIsSuccessfullyValidatedAndUserIsNotFound_thenSecurityContextIsNotSetAndExceptionIsThrown()
    {
        Assertions.fail();
    }

    @Test
    public void whenJWTTokenIsSuccessfullyValidatedAndUserIsNotFound_thenExceptionIsThrown()
    {
        Assertions.fail();
    }

    @Test
    public void whenJWTTokenValidationFails_thenExceptionIsThrown()
    {
        Assertions.fail();
    }
}