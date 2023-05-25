package com.jonathand.issuecrush.organization;

import jakarta.validation.constraints.NotBlank;

public record OrganizationNewRequest(
    @NotBlank(message = "You must provide a name for the new organization") String name) {

}
