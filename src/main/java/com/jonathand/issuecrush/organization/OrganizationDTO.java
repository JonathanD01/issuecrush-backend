package com.jonathand.issuecrush.organization;

import java.util.Date;

import com.jonathand.issuecrush.user.UserDTO;

public record OrganizationDTO(
    Long id,
    UserDTO owner,
    String name,
    Date createdAt,
    Date updatedAt) {

}
