package com.jonathand.issuecrush.ticket.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCommentUpdateRequest(
    @NotNull(message = "organizationUserId cannot be blank")
    Long organizationUserId,
    @NotBlank(message = "Content cannot be blank") String content) {

}
