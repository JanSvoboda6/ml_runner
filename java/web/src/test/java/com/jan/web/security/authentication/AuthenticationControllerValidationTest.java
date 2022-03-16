package com.jan.web.security.authentication;

import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthenticationControllerValidationTest
{
    private static final String BASE_URL = "http://localhost:";
    public static final String EMAIL = "user@email.com";
    public static final String INVALID_EMAIL = "user@@@email.com";
    public static final String PASSWORD = "TheStrongPassword_999";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenValidEmailAndPasswordIsPassedDuringRegistration_thenUserWillBeRegistered() throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("username", EMAIL);
        json.put("password", PASSWORD);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void whenInvalidEmailIsPassedDuringRegistration_thenUserWillNotBeRegistered() throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("username", INVALID_EMAIL);
        json.put("password", PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Email is not in a valid format!"));
    }

    @Test
    public void whenBlankEmailIsPassedDuringRegistration_thenUserWillNotBeRegistered_mvc() throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("username", Strings.EMPTY);
        json.put("password", PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Length of the email must be between 5 to 128 characters!"));
    }
}
