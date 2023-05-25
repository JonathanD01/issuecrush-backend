package com.jonathand.issuecrush.ticket.body;

import com.jonathand.issuecrush.ticket.Ticket;
import com.jonathand.issuecrush.ticket.TicketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketBodyService {

    private final TicketUtil ticketUtil;

    private final TicketBodyDTOMapper ticketBodyDTOMapper;

    /**
     * Retrieves the ticket body for a given ticket.
     *
     * @param ticketId the ID of the ticket
     * @return the ticket body DTO associated with the ticket
     */
    public TicketBodyDTO getTicketBodyForTicket(Long ticketId) {
        Ticket ticket = ticketUtil.getTicketById(ticketId);
        return ticketBodyDTOMapper.apply(ticket.getTicketBody());
    }

}
