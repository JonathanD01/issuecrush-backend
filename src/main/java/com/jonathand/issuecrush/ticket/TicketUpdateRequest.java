package com.jonathand.issuecrush.ticket;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record TicketUpdateRequest(
    @NotNull(message = "organizationUserId cannot be blank")
    Long organizationUserId,
    @Nullable
    String title,
    @Nullable
    String content,
    @Nullable
    String priority,
    @Nullable
    String department,
    @Nullable
    Boolean open,
    @Nullable
    Long assigned_agent
) {

}
