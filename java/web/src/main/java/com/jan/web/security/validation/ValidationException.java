package com.jan.web.security.validation;

public class ValidationException extends RuntimeException
{
    public ValidationException(String message)
    {
        super(message);
    }
}
