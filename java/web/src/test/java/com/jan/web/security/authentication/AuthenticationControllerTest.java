package com.jan.web.security.authentication;

import com.jan.web.security.ValidationException;
import com.jan.web.security.request.LoginRequest;
import com.jan.web.security.request.RegisterRequest;
import com.jan.web.security.role.Role;
import com.jan.web.security.role.RoleRepository;
import com.jan.web.security.role.RoleType;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserCreator;
import com.jan.web.security.user.UserDetailsImpl;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import com.jan.web.security.verification.*;
import org.assertj.core.api.Assertions;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
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
    private VerificationService verificationService;
    private VerificationTokenRepository verificationTokenRepository;
    private EmailService emailService;
    private VerificationToken verificationToken;
    private User user;

    @BeforeEach
    public void before()
    {
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        encoder = Mockito.mock(PasswordEncoder.class);
        jsonWebTokenUtility = Mockito.mock(JsonWebTokenUtility.class);
        userCreator = Mockito.mock(UserCreator.class);
        verificationService = Mockito.mock(VerificationService.class);
        verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
        emailService = Mockito.mock(EmailService.class);
        authenticationController = new AuthenticationController(authenticationManager,
                userRepository,
                roleRepository,
                encoder,
                jsonWebTokenUtility,
                userCreator,
                verificationService,
                verificationTokenRepository,
                emailService);

        verificationToken = Mockito.mock(VerificationToken.class);
        Mockito.when(verificationService.createVerificationToken(Mockito.any())).thenReturn(verificationToken);
        Mockito.when(verificationService.isUserVerificationServiceActivated()).thenReturn(true);

        user = new User(USERNAME, PASSWORD);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(user));
    }

    @Test
    public void whenNewUserTriesToRegister_thenUserIsRegistered()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        RegisterRequest request = createArtificialRegisterRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        authenticationController.registerUser(request);

        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    public void whenNewUserTriesToRegister_thenPasswordIsEncrypted()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        RegisterRequest request = createArtificialRegisterRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(user);

        Mockito.when(userRepository.save(user)).thenReturn(user);
        authenticationController.registerUser(request);
        Mockito.verify(encoder).encode(PASSWORD);
    }

    @Test
    public void whenAlreadyRegisteredUsernameIsUsed_thenRegistrationProcessReturnsBadRequest()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

        RegisterRequest request = createArtificialRegisterRequest();
        Assertions.assertThatThrownBy(() -> authenticationController.registerUser(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email is already taken!");
    }

    @Test
    public void whenUserIsRegistered_thenUserRoleShouldBeSetOnlyToUser()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        Role role = new Role();
        role.setName(RoleType.ROLE_USER);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.of(role));

        RegisterRequest request = createArtificialRegisterRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        authenticationController.registerUser(request);

        Assertions.assertThat(user.getRoles().size()).isEqualTo(1);

        Set<Role> roles = user.getRoles();
        RoleType actualRoleType = roles.stream().toList().get(0).getName();

        Assertions.assertThat(actualRoleType).isEqualTo(RoleType.ROLE_USER);
    }

    @Test
    public void whenUserTriesToLogIn_thenNewJWTTokenIsGenerated()
    {
        Mockito.when(authenticationManager.authenticate(USERNAME, PASSWORD)).thenReturn(user);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        Mockito.when(request.getUsername()).thenReturn(USERNAME);
        Mockito.when(request.getPassword()).thenReturn(PASSWORD);
        Mockito.when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);

        authenticationController.authenticateUser(request);

        Mockito.verify(jsonWebTokenUtility).generateJwtToken(user);
    }

    @Test
    public void whenUserTriesToLogIn_thenListOfRolesAreReturned() throws IOException
    {
        Mockito.when(authenticationManager.authenticate(USERNAME, PASSWORD)).thenReturn(createArtificialUser());
        LoginRequest request = Mockito.mock(LoginRequest.class);
        Mockito.when(request.getUsername()).thenReturn(USERNAME);
        Mockito.when(request.getPassword()).thenReturn(PASSWORD);
        Mockito.when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);

        ResponseEntity<?> response = authenticationController.authenticateUser(request);
        String body = new ObjectMapper().writeValueAsString(response.getBody());
        Assertions.assertThat(body).contains(RoleType.ROLE_USER.name());
    }

    @Test
    public void whenNotRegisteredUserTriesToLogIn_thenAuthenticationFails()
    {
        Mockito.when(authenticationManager.authenticate(USERNAME, PASSWORD)).thenThrow(new ValidationException("Email or password is invalid!"));
        LoginRequest request = Mockito.mock(LoginRequest.class);
        Mockito.when(request.getUsername()).thenReturn(USERNAME);
        Mockito.when(request.getPassword()).thenReturn(PASSWORD);
        Mockito.when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);

        Assertions.assertThatThrownBy(() -> authenticationController.authenticateUser(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email or password is invalid!");
    }

    @Test
    public void whenNewUserTriesToRegister_thenUserIsNotVerified()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        RegisterRequest request = createArtificialRegisterRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn((user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        authenticationController.registerUser(request);

        Assertions.assertThat(user.isVerified()).isFalse();
    }

    @Test
    public void whenNewUserTriesToRegister_thenVerificationCodeIsCreatedAndVerificationEmailIsSent()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        RegisterRequest request = createArtificialRegisterRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn((user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        authenticationController.registerUser(request);
        Mockito.verify(verificationService).createVerificationToken(user);
        Mockito.verify(emailService).sendEmail(Mockito.any(EmailContext.class));
    }

    @Test
    public void whenUserTriesToVerifyAccountWithValidToken_thenAccountIsVerified()
    {
        VerificationToken token = new VerificationToken("verification_token_value", LocalDateTime.now().minusHours(1), user);
        Mockito.when(verificationService.isVerificationTokenValid(token.getToken())).thenReturn(true);
        Mockito.when(verificationTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        authenticationController.verifyUserAccount(token.getToken());

        Mockito.verify(verificationService).isVerificationTokenValid(token.getToken());
        Mockito.verify(userRepository).save(Mockito.argThat(User::isVerified));
    }

    @Test
    public void whenUserTriesToVerifyAccountWithInvalidToken_thenAccountIsNotVerified()
    {
        VerificationToken token = new VerificationToken("verification_token_value", LocalDateTime.now().minusHours(1), user);
        Mockito.when(verificationService.isVerificationTokenValid(token.getToken())).thenReturn(false);

        authenticationController.verifyUserAccount(token.getToken());

        Mockito.verify(verificationService).isVerificationTokenValid(token.getToken());
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
    }

    @Test
    public void whenUserAccountIsVerified_thenJwtTokenForUserIsGenerated()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(USERNAME, PASSWORD)).thenReturn(user);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        Mockito.when(request.getUsername()).thenReturn(USERNAME);
        Mockito.when(request.getPassword()).thenReturn(PASSWORD);
        Mockito.when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);

        authenticationController.authenticateUser(request);

        Mockito.verify(jsonWebTokenUtility).generateJwtToken(user);
    }

    @Test
    public void whenUserAccountIsNotVerified_thenNoJwtTokenIsGenerated()
    {
        Mockito.when(authenticationManager.authenticate(USERNAME, PASSWORD)).thenThrow(new ValidationException("The user account is not verified!"));
        LoginRequest request = Mockito.mock(LoginRequest.class);
        Mockito.when(request.getUsername()).thenReturn(USERNAME);
        Mockito.when(request.getPassword()).thenReturn(PASSWORD);
        Mockito.when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);

        Assertions.assertThatThrownBy(() -> authenticationController.authenticateUser(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("The user account is not verified!");

        Mockito.verify(jsonWebTokenUtility, Mockito.times(0)).generateJwtToken(user);
    }

    private RegisterRequest createArtificialRegisterRequest()
    {
        RegisterRequest request = new RegisterRequest();
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