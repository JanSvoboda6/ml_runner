package com.jan.web.security.request;

import javax.validation.constraints.NotBlank;

/**
 * Represents a request for log in operation.
 */
public class LoginRequest
{
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
