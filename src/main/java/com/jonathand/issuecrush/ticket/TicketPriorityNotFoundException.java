package com.jonathand.issuecrush.ticket;

public class TicketPriorityNotFoundException extends RuntimeException {

    public TicketPriorityNotFoundException(String priorityArgument) {
        super("The priority " + priorityArgument + " was not found...");
    }

}
