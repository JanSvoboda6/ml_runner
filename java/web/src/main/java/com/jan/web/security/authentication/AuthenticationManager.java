package com.jan.web.security.authentication;

import com.jan.web.security.validation.ValidationException;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class used for an authentication of {@link User}.
 * Already hashed password should be passed.
 */
@Service
public class AuthenticationManager
{
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationManager(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public User authenticate(String username, String password)
    {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent())
        {
            if(!user.get().getPassword().equals(password))
            {
                throw new ValidationException("Email or password is invalid!");
            }
            if(!user.get().isVerified())
            {
                throw new ValidationException("The user account is not verified!");
            }
            return user.get();
        }
        throw new ValidationException("Email or password is invalid!");
    }
}
