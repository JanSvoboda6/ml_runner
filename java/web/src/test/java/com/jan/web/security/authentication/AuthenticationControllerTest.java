package com.jan.web.security.authentication;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class AuthenticationControllerTest
{

    @Test
    public void whenNewUserTriesToRegister_thenUserIsRegistered()
    {
        Assertions.fail();
    }

    @Test
    public void whenAlreadyRegisteredUsernameIsUsed_thenRegistrationProcessThrowsException()
    {
        Assertions.fail();
    }

    @Test
    public void whenAlreadyRegisteredEmailIsUsed_thenRegistrationProcessThrowsException()
    {
        Assertions.fail();
    }

    @Test
    public void whenUserIsRegistered_thenUserRoleShouldBeSetOnlyToUser()
    {
        //TODO Jan: Refactor role setting
        Assertions.fail();
    }

    @Test
    public void whenUserTriesToSignIn_thenNewJWTTokenIsGenerated()
    {
        Assertions.fail();
    }

    @Test
    public void whenUserTriesToSignIn_thenSecurityContextIsProperlySet()
    {
        Assertions.fail();
    }

    @Test
    public void whenUserTriesToSignIn_thenSetOfGrantedAuthoritiesIsUsed()
    {
        Assertions.fail();
    }

    @Test
    public void whenNotRegisteredUserTriesToSignIn_thenAuthenticationFails()
    {
        Assertions.fail();
    }
}