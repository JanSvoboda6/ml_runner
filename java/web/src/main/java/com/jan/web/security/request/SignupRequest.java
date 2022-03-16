package com.jan.web.security.request;

import javax.validation.constraints.*;

public class SignupRequest {

    @Email(message = "Email is not in a valid format!")
    @Size(min = 5, max = 128, message = "Length of the email must be between 5 to 128 characters!")
    private String username;

    @Pattern(regexp =  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()_*.<>!:])(?=\\S+$).{8,50}$",
            message = "Password is not strong enough!")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}