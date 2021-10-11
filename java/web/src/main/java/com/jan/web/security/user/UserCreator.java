package com.jan.web.security.user;

public class UserCreator
{
    public User createUser(String username, String email, String password)
    {
        return new User(username, email, password);
    }
}
