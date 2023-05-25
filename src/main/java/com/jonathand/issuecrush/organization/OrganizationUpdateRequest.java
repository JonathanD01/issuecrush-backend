package com.jonathand.issuecrush.organization;

import jakarta.validation.constraints.NotBlank;

public record OrganizationUpdateRequest(
    @NotBlank(message = "You must provide a new name for the organization")
    String name
) {

}
