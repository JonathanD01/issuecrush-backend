package com.jonathand.issuecrush.ticket.property;

import com.jonathand.issuecrush.ticket.TicketPriorityNotFoundException;

public enum TicketPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
    ;

    public static TicketPriority fromString(String priorityArgument) {
        try {
            return TicketPriority.valueOf(priorityArgument.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TicketPriorityNotFoundException(priorityArgument.toUpperCase());
        }
    }
}
