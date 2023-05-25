package com.jonathand.issuecrush.organization;

import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.ticket.TicketRepository;
import com.jonathand.issuecrush.ticket.comment.TicketCommentRepository;
import com.jonathand.issuecrush.user.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

public class OrganizationSecurityExpressions {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private OrganizationUtil organizationUtil;

    @Autowired
    private OrganizationUserUtil organizationUserUtil;

    /**
     * Checks if the current user is the owner of the organization.
     *
     * @param organizationId the ID of the organization
     * @return true if the current user is the owner of the organization, false otherwise
     */
    public boolean isUserOrganizationOwner(Long organizationId) {
        UserDetails userDetails = userUtil.getCurrentUserDetails();
        Organization organization = organizationUtil.getOrganizationById(organizationId);

        return organization.getCreator()
            .getUsername()
            .equals(userDetails.getUsername());
    }

    /**
     * Checks if the current user is a member of the organization.
     *
     * @param organizationId the ID of the organization
     * @return true if the current user is a member of the organization, false otherwise
     */
    public boolean isUserMemberOfOrganization(Long organizationId) {
        UserDetails userDetails = userUtil.getCurrentUserDetails();
        Organization organization = organizationUtil.getOrganizationById(organizationId);

        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserByEmailAndOrganization(
            userDetails.getUsername(), organization.getId());

        return organizationUser.getOrganization()
            .getId()
            .equals(organizationId);
    }

    /**
     * Checks if the current user can add an organization user.
     *
     * @param organizationId the ID of the organization
     * @return true if the current user can add an organization user, false otherwise
     */
    public boolean canUserAddOrganizationUser(Long organizationId) {
        UserDetails userDetails = userUtil.getCurrentUserDetails();
        Organization organization = organizationUtil.getOrganizationById(organizationId);

        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserByEmailAndOrganization(
            userDetails.getUsername(), organization.getId());

        return organizationUser.getRole()
            .hasGreaterOrEqualPriorityThan(OrganizationRole.ADMIN);
    }

    /**
     * Checks if the current user is in the organization associated with the ticket.
     *
     * @param ticketId the ID of the ticket
     * @return true if the current user is in the organization associated with the ticket, false otherwise
     */
    public boolean isUserInTicketOrganization(Long ticketId) {
        UserDetails userDetails = userUtil.getCurrentUserDetails();

        return ticketRepository.doesUserBelongToTicketOrganization(userDetails.getUsername(), ticketId);
    }

    /**
     * Checks if the current user is in the organization associated with the ticket comment.
     *
     * @param ticketCommentId the ID of the ticket comment
     * @return true if the current user is in the organization associated with the ticket comment, false otherwise
     */
    public boolean isUserInTicketCommentOrganization(Long ticketCommentId) {
        UserDetails userDetails = userUtil.getCurrentUserDetails();

        return ticketCommentRepository.doesUserBelongToTicketCommentOrganization(userDetails.getUsername(),
            ticketCommentId);
    }

}
