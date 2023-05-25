package com.jonathand.issuecrush.organization.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationUserUpdateRequest(
    @NotNull(message = "You must provide an organizationId") Long organizationId,
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "You must provide a valid email address")
    String email,
    @NotBlank(message = "You must provide a role") String role) {

}
