package com.jonathand.issuecrush.ticket.comment;

import java.util.function.Function;

import com.jonathand.issuecrush.organization.user.OrganizationUserDTOMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketCommentDTOMapper implements Function<TicketComment, TicketCommentDTO> {

    private final OrganizationUserDTOMapper organizationUserDTOMapper;

    @Override
    public TicketCommentDTO apply(TicketComment ticketComment) {
        return new TicketCommentDTO(
            ticketComment.getId(),
            ticketComment.getTicket()
                         .getId(),
            organizationUserDTOMapper.apply(ticketComment.getPublisher()),
            ticketComment.getCreatedAt(),
            ticketComment.getUpdatedAt(),
            ticketComment.getContent());
    }

}
