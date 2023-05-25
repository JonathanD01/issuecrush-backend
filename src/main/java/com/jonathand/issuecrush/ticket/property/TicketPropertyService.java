package com.jonathand.issuecrush.ticket.property;

import com.jonathand.issuecrush.ticket.Ticket;
import com.jonathand.issuecrush.ticket.TicketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketPropertyService {

    private final TicketUtil ticketUtil;

    private final TicketPropertyDTOMapper ticketPropertyDTOMapper;

    /**
     * Retrieves the ticket property for a given ticket.
     *
     * @param ticketId the ID of the ticket
     * @return the ticket property DTO associated with the ticket
     */
    public TicketPropertyDTO getTicketPropertyForTicket(Long ticketId) {
        Ticket ticket = ticketUtil.getTicketById(ticketId);
        return ticketPropertyDTOMapper.apply(ticket.getTicketProperty());
    }

}
