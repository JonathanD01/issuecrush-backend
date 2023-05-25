package com.jonathand.issuecrush.ticket;

import java.util.Date;

import com.jonathand.issuecrush.organization.user.OrganizationUserDTO;

public record TicketDTO(
    Long id,
    Long organizationId,
    String organizationName,
    String title,
    OrganizationUserDTO publisher,
    Date createdAt,
    Date updatedAt,
    boolean open
) {

}