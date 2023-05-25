package com.jonathand.issuecrush.user;

public record UserDTO(
    String firstName,
    String lastName,
    String email,
    String role,
    boolean enabled
) {

}
