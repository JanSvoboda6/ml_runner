package com.jan.web.security.authentication;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class AuthTokenFilterTest
{

    @Test
    public void whenAuthorizationHeaderIsPresentAndJWTTokenIsPresent_thenJWTTokenisParsed()
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