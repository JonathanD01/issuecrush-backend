package com.jonathand.issuecrush.ticket.comment;

import jakarta.validation.constraints.NotBlank;

public record TicketCommentCreateRequest(
    @NotBlank(message = "Content cannot be blank") String content) {

}
