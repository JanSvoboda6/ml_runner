package com.jan.web.security.validation;

/**
 * Exception that is thrown when a validation fails.
 */
public class ValidationException extends RuntimeException
{
    public ValidationException(String message)
    {
        super(message);
    }
}
