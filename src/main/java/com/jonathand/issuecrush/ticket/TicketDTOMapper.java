package com.jonathand.issuecrush.ticket;

import java.util.function.Function;

import com.jonathand.issuecrush.organization.user.OrganizationUserDTOMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketDTOMapper implements Function<Ticket, TicketDTO> {

    private final OrganizationUserDTOMapper organizationUserDTOMapper;

    @Override
    public TicketDTO apply(Ticket ticket) {
        return new TicketDTO(
            ticket.getId(),
            ticket.getOrganization()
                .getId(),
            ticket.getOrganization()
                .getName(),
            ticket.getTicketBody()
                .getTitle(),
            organizationUserDTOMapper.apply(ticket.getPublisher()),
            ticket.getCreatedAt(),
            ticket.getUpdatedAt(),
            ticket.isOpen());
    }

}
