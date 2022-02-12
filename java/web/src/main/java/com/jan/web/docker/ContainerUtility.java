package com.jan.web.docker;

import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ContainerUtility
{
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserRepository userRepository;
    private final ContainerRepository containerRepository;

    @Autowired
    public ContainerUtility(JsonWebTokenUtility jsonWebTokenUtility, UserRepository userRepository, ContainerRepository containerRepository)
    {
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userRepository = userRepository;
        this.containerRepository = containerRepository;
    }

    public long getContainerIdFromToken(String token)
    {
        String username = jsonWebTokenUtility.getUsernameFromJwtToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        if( user.isPresent())
        {
            return containerRepository.findByUserId(user.get().getId()).getId();
        }
        throw new RuntimeException("No user found!");
    }
}
