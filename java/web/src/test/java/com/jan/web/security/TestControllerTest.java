package com.jan.web.security;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestControllerTest
{

    @Test
    public void whenUserTriesToAccessAdminContent_thenUnauthorizedResponseIsReturned()
    {
        Assertions.fail();
    }

    @Test
    public void whenUserTriesToAccessUserContent_thenContentIsReturnedInResponse()
    {
        Assertions.fail();
    }

    @Test
    public void whenUnauthorizedUserTriesToAccessUserContent_thenNoContentIsReturned()
    {
        Assertions.fail();
    }

}