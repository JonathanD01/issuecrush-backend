package com.jonathand.issuecrush.ticket.comment;

public class TicketCommentNotFoundException extends RuntimeException {

    public TicketCommentNotFoundException(Long ticketCommentId) {
        super("TicketComment with id " + ticketCommentId + " was not found");
    }

}
