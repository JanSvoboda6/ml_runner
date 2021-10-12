package com.jan.web.security.user;

import org.springframework.stereotype.Service;

@Service
public class UserCreator
{
    public User createUser(String username, String email, String password)
    {
        return new User(username, email, password);
    }
}
