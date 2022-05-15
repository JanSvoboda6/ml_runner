package com.jan.web.security.verification;

import com.jan.web.security.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Verification Token is used for validating the user account via an existing email address.
 */
@Entity
public class VerificationToken
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String token;

    @Basic(optional = false)
    @Column(updatable = false)
    private LocalDateTime expiration;

    @ManyToOne
    private User user;

    public VerificationToken(String token, LocalDateTime expiration, User user)
    {
        this.token = token;
        this.expiration = expiration;
        this.user = user;
    }

    public VerificationToken()
    {

    }

    public int getId()
    {
        return id;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public LocalDateTime getExpiration()
    {
        return expiration;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}