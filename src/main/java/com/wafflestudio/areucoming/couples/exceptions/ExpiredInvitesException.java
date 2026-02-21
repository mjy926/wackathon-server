package com.wafflestudio.areucoming.couples.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExpiredInvitesException extends RuntimeException{
    public ExpiredInvitesException(String message) {
        super(message);
    }
}
