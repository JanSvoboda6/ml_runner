package com.jan.web.security.authentication;

import com.jan.web.security.ValidationException;
import com.jan.web.security.request.LoginRequest;
import com.jan.web.security.request.RegisterRequest;
import com.jan.web.security.response.JwtResponse;
import com.jan.web.security.response.MessageResponse;
import com.jan.web.security.role.Role;
import com.jan.web.security.role.RoleRepository;
import com.jan.web.security.role.RoleType;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserCreator;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import com.jan.web.security.verification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController
{
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserCreator userCreator;
    private final VerificationService verificationService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    @Value("${spring.mail.username}")
    private String FROM_EMAIL;

    @Value("${web.backend.api}")
    private String BACKEND_API;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder,
            JsonWebTokenUtility jsonWebTokenUtility,
            UserCreator userCreator,
            VerificationService verificationService,
            VerificationTokenRepository verificationTokenRepository,
            EmailService emailService)
    {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userCreator = userCreator;
        this.verificationService = verificationService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest)
    {
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
        {
            throw new ValidationException("Email is already taken!");
        }

        User user = userCreator.createUser(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new ValidationException("Role cannot be found!"));

        roles.add(userRole);
        user.setRoles(roles);
        user.setVerified(false);

        if (verificationService.isUserVerificationServiceActivated())
        {
            userRepository.save(user);
            sendVerificationEmailToUser(user);
        } else
        {
            user.setVerified(true);
            userRepository.save(user);
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
    {
        User user = authenticationManager.authenticate(loginRequest.getUsername(), encoder.encode(loginRequest.getPassword()));
        String jwtToken = jsonWebTokenUtility.generateJwtToken(user);

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwtToken, user.getId(), user.getUsername(), roles));
    }

    @GetMapping("/verification")
    public ResponseEntity<?> verifyUserAccount(@RequestParam("token") String token)
    {
        if(verificationService.isVerificationTokenValid(token))
        {
            Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
            if(verificationTokenOptional.isPresent())
            {
                User user = verificationTokenOptional.get().getUser();
                user.setVerified(true);
                userRepository.save(user);
                return ResponseEntity.ok("The user account has been verified!");
            }
        }
        return ResponseEntity.badRequest().body("The user account cannot be verified! Please check validity of a token.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException exception)
    {
        return ResponseEntity.badRequest().body(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    private void sendVerificationEmailToUser(User user)
    {
        VerificationToken verificationToken = verificationService.createVerificationToken(user);
        EmailContext emailContext = new EmailContext();
        emailContext.setFrom(FROM_EMAIL);
        emailContext.setTo(user.getUsername());
        emailContext.setTemplateLocation("verification");
        Context context = new Context();
        context.setVariable("link", BACKEND_API + "/api/auth/verification?token=" + verificationToken.getToken());
        emailContext.setContext(context);
        emailContext.setSubject("Dear user, please activate your account.");
        emailService.sendEmail(emailContext);
    }
}
