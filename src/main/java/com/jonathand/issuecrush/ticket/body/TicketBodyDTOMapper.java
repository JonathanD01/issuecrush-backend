package com.jonathand.issuecrush.ticket.body;

import java.util.function.Function;

public class TicketBodyDTOMapper implements Function<TicketBody, TicketBodyDTO> {

    @Override
    public TicketBodyDTO apply(TicketBody ticket) {
        return new TicketBodyDTO(ticket.getId(), ticket.getTitle(), ticket.getContent());
    }

}
