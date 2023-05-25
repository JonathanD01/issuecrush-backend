package com.jonathand.issuecrush.auth;

import org.springframework.security.core.AuthenticationException;

public class AccessDeniedException extends AuthenticationException {

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException(String message) {
        super(message);
    }

}
