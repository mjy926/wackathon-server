package com.wafflestudio.areucoming.couples.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CoupleNotFoundException extends RuntimeException{
    public CoupleNotFoundException(String message) {
        super(message);
    }
}
