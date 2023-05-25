package com.jonathand.issuecrush.ticket.property;

import java.util.function.Function;

import com.jonathand.issuecrush.organization.user.OrganizationUserDTOMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketPropertyDTOMapper implements Function<TicketProperty, TicketPropertyDTO> {

    private final OrganizationUserDTOMapper organizationUserDTOMapper;

    @Override
    public TicketPropertyDTO apply(TicketProperty ticketProperty) {
        return new TicketPropertyDTO(
            ticketProperty.getId(),
            ticketProperty.getPriority()
                .name()
                .toUpperCase(),
            ticketProperty.getDepartment()
                .name()
                .toUpperCase(),
            organizationUserDTOMapper.apply(ticketProperty.getAssigned_agent()));
    }

}
