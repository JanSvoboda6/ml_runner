package com.jan.web.security.authentication;

import com.jan.web.security.validation.ValidationException;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class used for an authentication of {@link User}.
 */
@Service
public class AuthenticationManager
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationManager(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String username, String password)
    {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent())
        {
            if(!passwordEncoder.matches(password, user.get().getPassword()))
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
