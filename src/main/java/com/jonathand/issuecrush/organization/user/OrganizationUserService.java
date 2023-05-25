package com.jonathand.issuecrush.organization.user;

import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.organization.OrganizationRoleNotFoundException;
import com.jonathand.issuecrush.organization.OrganizationUtil;
import com.jonathand.issuecrush.response.ResponseMissingParameterException;
import com.jonathand.issuecrush.ticket.TicketRepository;
import com.jonathand.issuecrush.ticket.comment.TicketCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationUserService {

    private final TicketRepository ticketRepository;

    private final TicketCommentRepository ticketCommentRepository;

    private final OrganizationUserRepository organizationUserRepository;

    private final OrganizationUserDTOMapper organizationUserDTOMapper;

    private final OrganizationUtil organizationUtil;

    private final OrganizationUserUtil organizationUserUtil;

    /**
     * Retrieves the organization user based on the provided organization ID and user email.
     *
     * @param organizationId the ID of the organization to match
     * @param userEmail      the email of the user to match (case-insensitive)
     * @return the OrganizationUserDTO representing the organization user
     */
    // TODO TEST
    public OrganizationUserDTO getOrganizationUser(Long organizationId, String userEmail) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        // Retrieve organization user
        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserByEmailAndOrganization(
            userEmail,
            organizationId);
        return organizationUserDTOMapper.apply(organizationUser);
    }

    /**
     * Retrieves the organization user based on the provided organization ID and organization user ID.
     *
     * @param organizationId     the ID of the organization to match
     * @param organizationUserId the ID of the organization user to retrieve
     * @return the OrganizationUserDTO representing the organization user
     * @throws ResponseMissingParameterException if the organization user ID is missing or null
     */
    // TODO TEST
    public OrganizationUserDTO getOrganizationUser(Long organizationId, Long organizationUserId) {
        // If organizationUserId is null, then throw ResponseMissingParameterException
        // organizationUserId can be null because it is not a required parameter, but
        // it should not be null when the method is called
        if (organizationUserId == null) {
            throw new ResponseMissingParameterException("organizationUserId", "long");
        }

        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        // Retrieve organization user
        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserById(organizationUserId);
        return organizationUserDTOMapper.apply(organizationUser);
    }

    /**
     * Updates an organization user based on the provided update request.
     *
     * @param userUpdateRequest the OrganizationUserUpdateRequest containing the update information
     * @return the updated OrganizationUserDTO
     * @throws OrganizationRoleNotFoundException if the role from {@link OrganizationUserUpdateRequest#role()}
     *                                           cannot be cast to {@link OrganizationRole}
     * @see OrganizationUserUpdateRequest
     * @see OrganizationRole
     */
    public OrganizationUserDTO updateOrganizationUser(OrganizationUserUpdateRequest userUpdateRequest) {
        // Retrieve organization id from userUpdateRequest
        Long organizationId = userUpdateRequest.organizationId();

        // Retrieve email for user to update from userUpdateRequest
        String userEmailToUpdate = userUpdateRequest.email();

        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        // Retrieve organization user
        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserByEmailAndOrganization(
            userEmailToUpdate, organizationId);

        // Retrieve role argument from userUpdateRequest
        String roleArgument = userUpdateRequest.role();
        OrganizationRole organizationRole;

        // Try to convert roleArgument to organizationRole
        organizationRole = OrganizationRole.fromString(roleArgument);

        // Set the new role for organization user
        organizationUser.setRole(organizationRole);

        // Save organization user
        organizationUserRepository.save(organizationUser);
        return organizationUserDTOMapper.apply(organizationUser);
    }

    /**
     * Retrieves statistics for the organization user based on the provided organization ID and organization user ID.
     *
     * @param organizationId     the ID of the organization to match
     * @param organizationUserId the ID of the organization user to retrieve statistics for
     * @return the OrganizationUserStatisticsDTO containing the statistics
     * @throws OrganizationUserNotFoundException if the organization user does not exist
     */
    // TODO TEST
    public OrganizationUserStatisticsDTO getOrganizationUserStatistics(Long organizationId, Long organizationUserId) {
        // Validate organization exists
        organizationUserUtil.validateOrganizationUserDoesExists(organizationUserId);

        long totalTickets = ticketRepository.countByOrganization_IdAndPublisher_Id(organizationId, organizationUserId);
        long totalTicketComments = ticketCommentRepository.countByPublisher_Id(organizationUserId);
        long openTickets = ticketRepository.countByOrganization_IdAndPublisher_IdAndOpenTrue(
            organizationId,
            organizationUserId);
        long closedTickets = ticketRepository.countByOrganization_IdAndPublisher_IdAndOpenFalse(
            organizationId,
            organizationUserId);

        return new OrganizationUserStatisticsDTO(totalTickets, totalTicketComments, openTickets, closedTickets);
    }

}
