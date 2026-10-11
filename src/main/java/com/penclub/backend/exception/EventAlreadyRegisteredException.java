package com.penclub.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EventAlreadyRegisteredException extends RuntimeException {

    public EventAlreadyRegisteredException(String message) {
        super(message);
    }
}
