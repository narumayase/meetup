package com.baglietto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotValidRoleException extends RuntimeException {

    public NotValidRoleException(String message) {
        super(message);
    }
}
