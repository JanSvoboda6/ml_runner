package com.jan.web.security.authentication;

import com.jan.web.security.request.SignupRequest;
import com.jan.web.security.role.Role;
import com.jan.web.security.role.RoleRepository;
import com.jan.web.security.role.RoleType;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserCreator;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@RunWith(SpringRunner.class)
public class AuthenticationControllerTest
{
    public static final String PASSWORD = "password";
    public static final String USERNAME = "TomTheUser";
    public static final String EMAIL = "user@mail.com";

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder encoder;
    private  JsonWebTokenUtility jsonWebTokenUtility;
    private AuthenticationController authenticationController;
    private UserCreator userCreator;

    @BeforeEach
    public void before()
    {
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        encoder = Mockito.mock(PasswordEncoder.class);
        jsonWebTokenUtility = Mockito.mock(JsonWebTokenUtility.class);
        userCreator = Mockito.mock(UserCreator.class);
        authenticationController = new AuthenticationController(authenticationManager, userRepository, roleRepository, encoder, jsonWebTokenUtility, userCreator);
    }

    @Test
    public void whenNewUserTriesToRegister_thenUserIsRegistered()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        SignupRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        Mockito.when(userCreator.createUser(request.getUsername(), request.getEmail(), request.getPassword())).thenReturn(createArtificialUser());

        authenticationController.registerUser(request);

        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    public void whenNewUserTriesToRegister_thenPasswordIsEncrypted()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        SignupRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        Mockito.when(userCreator.createUser(request.getUsername(), request.getEmail(), request.getPassword())).thenReturn(createArtificialUser());

        authenticationController.registerUser(request);

        Mockito.verify(encoder).encode(PASSWORD);
    }

    @Test
    public void whenAlreadyRegisteredUsernameIsUsed_thenRegistrationProcessReturnsBadRequest()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

        SignupRequest request = createArtificialSignupRequest();
        ResponseEntity<?> responseEntity = authenticationController.registerUser(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void whenAlreadyRegisteredEmailIsUsed_thenRegistrationProcessReturnsBadRequest()
    {
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        SignupRequest request = createArtificialSignupRequest();
        ResponseEntity<?> responseEntity = authenticationController.registerUser(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void whenUserIsRegistered_thenUserRoleShouldBeSetOnlyToUser()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

        Role role = new Role();
        role.setName(RoleType.ROLE_USER);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.of(role));

        SignupRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getEmail(), request.getPassword())).thenReturn(user);

        authenticationController.registerUser(request);

        Assertions.assertEquals(1, user.getRoles().size());

        Set<Role> roles = user.getRoles();
        RoleType actualRoleType = roles.stream().toList().get(0).getName();

        Assertions.assertEquals(RoleType.ROLE_USER, actualRoleType);
    }

    @Test
    public void whenUserTriesToSignIn_thenNewJWTTokenIsGenerated()
    {
        Assertions.fail();
    }

    @Test
    public void whenUserTriesToSignIn_thenSecurityContextIsProperlySet()
    {
        Assertions.fail();
    }

    @Test
    public void whenUserTriesToSignIn_thenSetOfGrantedAuthoritiesIsUsed()
    {
        Assertions.fail();
    }

    @Test
    public void whenNotRegisteredUserTriesToSignIn_thenAuthenticationFails()
    {
        Assertions.fail();
    }

    private SignupRequest createArtificialSignupRequest()
    {
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME);
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);
        return request;
    }

    private User createArtificialUser()
    {
        return new User(USERNAME, EMAIL, PASSWORD);
    }
}