package com.jonathand.issuecrush.ticket;

import com.jonathand.issuecrush.organization.OrganizationAction;
import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class TicketUtil {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OrganizationUserUtil organizationUserUtil;

    /**
     * Retrieves a ticket by its ID
     *
     * @param ticketId the ID of the ticket
     * @return the Ticket object with the specified ID
     * @throws TicketNotFoundException if the ticket is not found
     */
    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    }

    /**
     * Validates if a ticket with specified ID exists in the system.
     *
     * @param ticketId The ID of the ticket to validate
     * @throws TicketNotFoundException If the ticket with the specified ID is not found.
     */
    public void validateTicketExists(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException(ticketId);
        }
    }

    /**
     * Validates whether the user has authorization to perform an action on the ticket.
     *
     * @param organizationAction the action to validate
     * @param ticketId           the ID of the ticket
     * @param organizationUserId the ID of the organization user
     * @throws TicketUnauthorizedActionException if the user is not authorized to perform the action
     */
    public void validateUserAuthorizationForTicketAction(OrganizationAction organizationAction, Long organizationUserId,
                                                         Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserById(organizationUserId);
        OrganizationRole roleRequired = organizationAction.getRoleRequired();

        boolean hasPermission = organizationUser.getRole()
            .hasGreaterOrEqualPriorityThan(roleRequired);
        boolean isAuthor = ticket.getPublisher()
            .getId()
            .equals(organizationUser.getId());
        if (!hasPermission && !isAuthor) {
            throw new TicketUnauthorizedActionException(roleRequired);
        }
    }

}
