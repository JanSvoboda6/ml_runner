package com.jan.web.security.authentication;

import com.jan.web.security.request.LoginRequest;
import com.jan.web.security.request.SignupRequest;
import com.jan.web.security.role.Role;
import com.jan.web.security.role.RoleRepository;
import com.jan.web.security.role.RoleType;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserCreator;
import com.jan.web.security.user.UserDetailsImpl;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Set;

@RunWith(SpringRunner.class)
public class AuthenticationControllerTest
{
    public static final String PASSWORD = "password";
    public static final String USERNAME = "user@email.com";

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder encoder;
    private  JsonWebTokenUtility jsonWebTokenUtility;
    private AuthenticationController authenticationController;
    private UserCreator userCreator;
    private AuthenticationListener authenticationListener;

    @BeforeEach
    public void before()
    {
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        encoder = Mockito.mock(PasswordEncoder.class);
        jsonWebTokenUtility = Mockito.mock(JsonWebTokenUtility.class);
        userCreator = Mockito.mock(UserCreator.class);
        authenticationListener = Mockito.mock(AuthenticationListener.class);
        authenticationController = new AuthenticationController(authenticationManager, userRepository, roleRepository, encoder, jsonWebTokenUtility, userCreator, authenticationListener);
    }

    @Test
    public void whenNewUserTriesToRegister_thenUserIsRegistered()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        SignupRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(createArtificialUser());

        authenticationController.registerUser(request);

        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    public void whenNewUserTriesToRegister_thenPasswordIsEncrypted()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        SignupRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(createArtificialUser());

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
    public void whenUserIsRegistered_thenUserRoleShouldBeSetOnlyToUser()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        Role role = new Role();
        role.setName(RoleType.ROLE_USER);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.of(role));

        SignupRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(user);

        authenticationController.registerUser(request);

        Assertions.assertEquals(1, user.getRoles().size());

        Set<Role> roles = user.getRoles();
        RoleType actualRoleType = roles.stream().toList().get(0).getName();

        Assertions.assertEquals(RoleType.ROLE_USER, actualRoleType);
    }

    @Test
    public void whenUserTriesToSignIn_thenNewJWTTokenIsGenerated()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        authenticationController.authenticateUser(request);

        Mockito.verify(jsonWebTokenUtility).generateJwtToken(authentication);
    }

    @Test
    public void whenUserTriesToSignIn_thenSecurityContextIsProperlySet()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        authenticationController.authenticateUser(request);
        Assertions.assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void whenUserTriesToSignIn_thenSetOfGrantedAuthoritiesIsUsed() throws IOException
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = UserDetailsImpl.build(createArtificialUser());
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        ResponseEntity<?> response = authenticationController.authenticateUser(request);
        String body = new ObjectMapper().writeValueAsString(response.getBody());

        Assertions.assertTrue(body.contains(RoleType.ROLE_USER.name()));
    }

    @Test
    public void whenNotRegisteredUserTriesToSignIn_thenAuthenticationFails()
    {
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("Bad Credentials Exception"));
        LoginRequest request = Mockito.mock(LoginRequest.class);
        ResponseEntity<?> responseEntity = authenticationController.authenticateUser(request);
        int BAD_REQUEST_CODE = 400;
        Assertions.assertEquals(BAD_REQUEST_CODE, responseEntity.getStatusCodeValue());
    }

    @Test
    public void whenUserIsSuccessfullySignedIn_thenListenerWillBeCalled()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        Long USER_ID = 999L;
        Mockito.when(userDetails.getId()).thenReturn(USER_ID);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        authenticationController.authenticateUser(request);
        Mockito.verify(authenticationListener).onSuccessfulLogin(USER_ID);
    }

    private SignupRequest createArtificialSignupRequest()
    {
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        return request;
    }

    private User createArtificialUser()
    {
        User user = new User(USERNAME, PASSWORD);
        user.setRoles(Set.of(new Role(RoleType.ROLE_USER)));
        return user;
    }
}