package com.jan.web.security.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
public class UserDetailsServiceImplTest
{
    public static final String USERNAME = "user@email.com";
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsServiceImpl service;

    @Test
    public void whenUserIsFound_thenUserIsReturned()
    {
        User user = new User(USERNAME, "password");
        userRepository.save(user);
        Assertions.assertNotNull(service.loadUserByUsername(USERNAME));
    }

    @Test
    public void whenUserIsNotFound_thenExceptionIsThrown()
    {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("NonExistingUsername"));
    }

}