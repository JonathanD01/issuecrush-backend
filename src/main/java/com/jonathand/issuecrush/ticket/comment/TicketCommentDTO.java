package com.jonathand.issuecrush.ticket.comment;

import java.util.Date;

import com.jonathand.issuecrush.organization.user.OrganizationUserDTO;

public record TicketCommentDTO(
    Long id,
    Long ticketId,
    OrganizationUserDTO publisher,
    Date createdAt,
    Date updatedAt,
    String content) {

}
