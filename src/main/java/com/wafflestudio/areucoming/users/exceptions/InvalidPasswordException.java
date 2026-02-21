package com.wafflestudio.areucoming.users.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class InvalidPasswordException extends RuntimeException
{
    public InvalidPasswordException(String message)
    {
        super(message);
    }
}
