package com.jonathand.issuecrush.auth;

public class EmailAlreadyTakenException extends RuntimeException {

    public EmailAlreadyTakenException(String email) {
        super("Email " + email + " is already taken");
    }

}
