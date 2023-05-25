package com.jonathand.issuecrush.organization.user;

public record OrganizationUserDTO(
    Long id,
    String email,
    String firstName,
    String lastName,
    String role
) {

}