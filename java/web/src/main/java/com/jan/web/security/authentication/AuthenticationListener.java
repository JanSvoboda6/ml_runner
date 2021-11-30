package com.jan.web.security.authentication;

public interface AuthenticationListener
{
    void onSuccessfulLogin(Long userId);
}
