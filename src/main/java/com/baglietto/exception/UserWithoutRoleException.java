package com.baglietto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserWithoutRoleException extends RuntimeException {

    public UserWithoutRoleException(String message) {
        super(message);
    }
}
