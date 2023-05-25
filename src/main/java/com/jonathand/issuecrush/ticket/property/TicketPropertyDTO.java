package com.jonathand.issuecrush.ticket.property;

import com.jonathand.issuecrush.organization.user.OrganizationUserDTO;

public record TicketPropertyDTO(
    Long id,
    String priority,
    String department,
    OrganizationUserDTO assigned_agent
) {

}