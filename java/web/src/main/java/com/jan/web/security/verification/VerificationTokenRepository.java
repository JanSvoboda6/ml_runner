package com.jan.web.security.verification;

import com.jan.web.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository providing access to {@link VerificationToken}.
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>
{
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
    void deleteByUser(User user);
}