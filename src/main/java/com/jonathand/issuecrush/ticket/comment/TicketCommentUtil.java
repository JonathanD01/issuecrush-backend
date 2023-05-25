package com.jonathand.issuecrush.ticket.comment;

import com.jonathand.issuecrush.organization.OrganizationAction;
import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.ticket.TicketUnauthorizedActionException;
import org.springframework.beans.factory.annotation.Autowired;

public class TicketCommentUtil {

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    @Autowired
    private OrganizationUserUtil organizationUserUtil;

    /**
     * Retrieves a ticket content by its ID.
     *
     * @param ticketCommentId the ID of the ticket content
     * @return the ticket content
     * @throws TicketCommentNotFoundException if the ticket content is not found
     */
    public TicketComment getTicketComment(Long ticketCommentId) {
        return ticketCommentRepository.findById(ticketCommentId)
            .orElseThrow(() -> new TicketCommentNotFoundException(ticketCommentId));
    }

    /**
     * Validates whether the user has authorization to perform an action on the ticket comment.
     *
     * @param organizationAction the action user is performing
     * @param organizationUserId the ID of the organization user
     * @param ticketCommentId    the ID of the ticket comment
     * @throws TicketUnauthorizedActionException if the user is not authorized to perform the action
     */
    public void validateUserAuthorizationForTicketCommentAction(OrganizationAction organizationAction,
                                                                Long organizationUserId, Long ticketCommentId) {
        TicketComment ticket = getTicketComment(ticketCommentId);
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
