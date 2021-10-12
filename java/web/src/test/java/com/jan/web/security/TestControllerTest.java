package com.jan.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerTest
{
    public static final int FORBIDDEN_STATUS_CODE = 403;

    @Autowired
    TestController controller;

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser
    public void whenUserTriesToAccessAdminContent_thenUnauthorizedResponseIsReturned() throws Exception
    {
        mockMvc.perform(get("/api/test/admin")).andExpect(status().is(FORBIDDEN_STATUS_CODE));
    }
}