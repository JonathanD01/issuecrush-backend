package com.jonathand.issuecrush.organization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationAddUserRequest(

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "You must provide a valid email address")
    String email
) {

}
