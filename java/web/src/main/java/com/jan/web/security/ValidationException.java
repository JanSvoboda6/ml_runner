package com.jan.web.security;

public class ValidationException extends RuntimeException
{
    public ValidationException(String message)
    {
        super(message);
    }
}
