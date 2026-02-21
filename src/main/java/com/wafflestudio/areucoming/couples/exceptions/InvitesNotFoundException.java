package com.wafflestudio.areucoming.couples.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvitesNotFoundException extends RuntimeException {
    public InvitesNotFoundException(String message) {
        super(message);
    }
}
