package com.jonathand.issuecrush.organization.user;

public record OrganizationUserStatisticsDTO(
    long totalTickets,
    long totalTicketComments,
    long openTickets,
    long closedTickets
) {

}
