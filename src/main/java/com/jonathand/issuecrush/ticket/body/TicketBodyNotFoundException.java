package com.jonathand.issuecrush.ticket.body;

public class TicketBodyNotFoundException extends RuntimeException {

    public TicketBodyNotFoundException(Long ticketBodyId) {
        super("TicketBody with id " + ticketBodyId + " was not found");
    }

}
