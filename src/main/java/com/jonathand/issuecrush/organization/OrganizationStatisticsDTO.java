package com.jonathand.issuecrush.organization;

public record OrganizationStatisticsDTO(
    long totalTickets,
    long totalTicketComments,
    long openTickets,
    long closedTickets,
    long totalOrganizationUsers
) {

}
