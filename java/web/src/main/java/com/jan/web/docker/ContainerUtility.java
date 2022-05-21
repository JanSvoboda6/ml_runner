package com.jan.web.docker;

import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import com.jan.web.security.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class utilized for getting a container ID from JWT token.
 */
@Service
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
        if (user.isPresent())
        {
            Optional<ContainerEntity> container = containerRepository.findByUserId(user.get().getId());
            if (container.isPresent())
            {
                return container.get().getId();
            }
            else
            {
                throw new ValidationException("No container found!");
            }
        }
        throw new ValidationException("No user found!");
    }
}
