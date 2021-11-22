package com.jan.web.security.user;

import org.springframework.stereotype.Service;

@Service
public class UserCreator
{
    public User createUser(String username, String password)
    {
        return new User(username, password);
    }
}
