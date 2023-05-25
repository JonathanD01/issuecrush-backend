package com.jonathand.issuecrush.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCreateRequest(
    @NotBlank(message = "The ticket must have a title") String title,
    @NotBlank(message = "The ticket must have a content") String content,
    @NotBlank(message = "The ticket must have a priority") String priority,
    @NotBlank(message = "The ticket department cannot be empty") String department,
    @NotNull(message = "The ticket must be either open or closed") boolean open) {

}
