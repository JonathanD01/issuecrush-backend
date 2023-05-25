package com.jonathand.issuecrush.ticket.property;

public class TicketPropertyNotFoundException extends RuntimeException {

    public TicketPropertyNotFoundException(Long ticketPropertyId) {
        super("TicketProperty with id " + ticketPropertyId + " was not found");
    }

}
