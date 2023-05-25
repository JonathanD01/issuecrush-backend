package com.jonathand.issuecrush.response;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

public record APIErrorDTO(
    String message,
    HttpStatus httpStatus,
    ZonedDateTime timestamp
) {

}
