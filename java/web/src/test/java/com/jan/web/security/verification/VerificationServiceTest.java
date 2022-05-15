package com.jan.web.security.verification;

import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
@Transactional
public class VerificationServiceTest
{
    public static String EMAIL = "user@test.com";
    public static String PASSWORD = "StrongPassword_999";

    @Autowired
    VerificationService verificationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VerificationTokenRepository tokenRepository;
    private User user;

    @BeforeEach
    public void before()
    {
        user = userRepository.save(new User(EMAIL, PASSWORD));
    }

    @AfterEach
    public void after()
    {
        tokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    @Test
    public void whenVerificationTokenIsCreated_thenItIsPersisted()
    {
        verificationService.createVerificationToken(user);
        Assertions.assertThat(tokenRepository.findByUser(user)).isPresent();
    }

    @Test
    public void whenVerificationTokenIsCreated_thenTokenFieldIsNotBlank()
    {
        verificationService.createVerificationToken(user);
        Optional<VerificationToken> tokenOptional = tokenRepository.findByUser(user);
        if (tokenOptional.isEmpty())
        {
            Assertions.fail("No Token has been generated!");
        }
        Assertions.assertThat(tokenOptional.get().getToken()).isNotBlank();
    }

    @Test
    public void whenVerificationTokenIsUnexpiredAndBelongsToUser_thenTokenIsValid()
    {
        verificationService.createVerificationToken(user);
        Optional<VerificationToken> tokenOptional = tokenRepository.findByUser(user);
        if (tokenOptional.isEmpty())
        {
            Assertions.fail("No Token has been generated!");
        }
        VerificationToken token = tokenOptional.get();
        Assertions.assertThat(verificationService.isVerificationTokenValid(token.getToken())).isTrue();
    }

    @Test
    public void whenVerificationTokenHasNotBeenPersisted_thenTokenIsInvalid()
    {
        VerificationToken token = new VerificationToken("random_token_value", LocalDateTime.now(), user);
        Assertions.assertThat(verificationService.isVerificationTokenValid(token.getToken())).isFalse();
    }

    @Test
    public void whenVerificationTokenExpirationTimeIsBeforeCurrentTime_thenTokenIsInvalid()
    {
        VerificationTokenRepository verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
        VerificationToken token = new VerificationToken("random_token_value", LocalDateTime.now().minusSeconds(1), user);
        Mockito.when(verificationTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        VerificationService verificationService = new VerificationService(verificationTokenRepository);

        Assertions.assertThat(verificationService.isVerificationTokenValid(token.getToken())).isFalse();
    }
}