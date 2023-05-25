package com.jonathand.issuecrush.organization;

import java.util.List;
import java.util.stream.Collectors;

import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserDTO;
import com.jonathand.issuecrush.organization.user.OrganizationUserDTOMapper;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.ticket.TicketRepository;
import com.jonathand.issuecrush.ticket.comment.TicketCommentRepository;
import com.jonathand.issuecrush.user.User;
import com.jonathand.issuecrush.user.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final OrganizationUserRepository organizationUserRepository;

    private final TicketRepository ticketRepository;

    private final TicketCommentRepository ticketCommentRepository;

    private final OrganizationDTOMapper organizationDTOMapper;

    private final OrganizationUserDTOMapper organizationUserDTOMapper;

    private final UserUtil userUtil;

    private final OrganizationUtil organizationUtil;

    private final OrganizationUserUtil organizationUserUtil;

    /**
     * Retrieves a list of organizations for a user based on the provided organization name and user email. The
     * results are paginated
     * based on the provided pageable object.
     *
     * @param userEmail The email of the user.
     * @param pageable  pagination information
     * @return A list of organization DTOs matching the specified criteria.
     */
    public List<OrganizationDTO> getOrganizationsForUser(String userEmail, Pageable pageable) {
        // Validate user exists
        userUtil.validateUserExistsByEmail(userEmail);

        Page<Organization> organizationPage = organizationRepository.findOrganizationsForEmail(userEmail, pageable);

        return organizationPage.stream()
            .map(organizationDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of organizations for a user based on the provided organization name and user email. If a
     * non-blank organization name is provided, the organizations will be filtered by name. The results are paginated
     * based on the provided pageable object.
     *
     * @param organizationName The name of the organization to filter by. Can be blank to retrieve all
     *                         organizations.
     * @param userEmail        The email of the user.
     * @param pageable         pagination information.
     * @return A list of organization DTOs matching the specified criteria.
     */
    public List<OrganizationDTO> getOrganizationsForUserByOrganizationName(String organizationName, String userEmail,
                                                                           Pageable pageable) {
        // Validate user exists
        userUtil.validateUserExistsByEmail(userEmail);

        Page<Organization> organizationPage = organizationRepository.findByUsers_User_EmailAndNameContainsIgnoreCase(
            userEmail, organizationName, pageable);

        return organizationPage.stream()
            .map(organizationDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Adds a new organization with the provided information.
     *
     * @param userEmail              The email of the user creating the organization.
     * @param organizationNewRequest The request object containing the details of the new organization.
     * @return The DTO representation of the newly created organization.
     */
    public OrganizationDTO addNewOrganization(String userEmail, OrganizationNewRequest organizationNewRequest) {
        User user = userUtil.getUserByEmail(userEmail);

        // Create new organization
        Organization newOrganization = Organization.builder()
            .name(organizationNewRequest.name())
            .creator(user)
            .build();

        // Save new organization
        organizationRepository.save(newOrganization);

        // Create organization user which will be the
        // owner of the organization
        OrganizationUser newOrganizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.OWNER)
            .organization(newOrganization)
            .build();

        // Save organization user
        organizationUserRepository.save(newOrganizationUser);
        return organizationDTOMapper.apply(newOrganization);
    }

    /**
     * Updates an existing organization.
     *
     * @param organizationId            The ID of the organization to update
     * @param organizationUpdateRequest The request object containing the details of the new organization.
     * @return The DTO representation of the updated organization.
     */
    public OrganizationDTO updateOrganization(Long organizationId,
                                              OrganizationUpdateRequest organizationUpdateRequest) {
        // Get organization to be updated
        Organization organizationToBeUpdated = organizationUtil.getOrganizationById(organizationId);

        // Update the name
        organizationToBeUpdated.setName(organizationUpdateRequest.name());

        // Save organization
        organizationRepository.save(organizationToBeUpdated);
        return organizationDTOMapper.apply(organizationToBeUpdated);
    }

    /**
     * Updates an existing organization.
     *
     * @param organizationId The ID of the organization to delete
     * @return The id representing the deleted organization.
     */
    public Long deleteOrganization(Long organizationId) {
        // Get organization to delete
        Organization organizationToBeDeleted = organizationUtil.getOrganizationById(organizationId);

        // Delete organization
        organizationRepository.deleteById(organizationToBeDeleted.getId());
        return organizationId;
    }

    /**
     * Get an organization.
     *
     * @param organizationId The ID of the organization
     * @return The DTO representation of the organization to get.
     */
    public OrganizationDTO getOrganization(Long organizationId) {
        // Get organization & return
        Organization organization = organizationUtil.getOrganizationById(organizationId);
        return organizationDTOMapper.apply(organization);
    }

    // TODO TEST

    /**
     * Get the statistics for an organization.
     *
     * @param organizationId The ID of the organization to get
     * @return The DTO representation of an organizations statistics
     */
    public OrganizationStatisticsDTO getOrganizationStatistics(Long organizationId) {
        long totalTickets = ticketRepository.countByOrganization_Id(organizationId);
        long totalTicketComments = ticketCommentRepository.countByPublisher_Organization_Id(organizationId);
        long openTickets = ticketRepository.countByOrganization_IdAndOpenTrue(organizationId);
        long closedTickets = ticketRepository.countByOrganization_IdAndOpenFalse(organizationId);
        long totalOrganizationUsers = organizationUserRepository.countByOrganization_Id(organizationId);

        return new OrganizationStatisticsDTO(
            totalTickets, totalTicketComments, openTickets, closedTickets, totalOrganizationUsers);
    }

    // TODO UPDATE TESTS?

    /**
     * Retrieves a list of the users in an organization. The results are paginated based on the provided pageable
     * object.
     *
     * @param organizationId The ID of the organization
     * @param pageable       pagination information.
     * @return A list of organization-user DTOs in the organization.
     */
    public List<OrganizationUserDTO> getOrganizationUsers(Long organizationId, Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);
        return organizationUserRepository.findByOrganization_Id(organizationId, pageable)
            .stream()
            .map(organizationUserDTOMapper)
            .collect(Collectors.toList());
    }

    // TODO TEST

    /**
     * Retrieves a list of the users in an organization. The users are filtered by userEmail. The results are paginated
     * based on the provided pageable object.
     *
     * @param organizationId The ID of the organization
     * @param userEmail      The email to filter by
     * @param pageable       pagination information.
     * @return A list of organization-user DTOs in the organization.
     */
    public List<OrganizationUserDTO> getOrganizationUsersByEmail(Long organizationId, String userEmail,
                                                                 Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);
        return organizationUserRepository.findByOrganization_IdAndUser_EmailContainsIgnoreCase(
                organizationId, userEmail, pageable)
            .stream()
            .map(organizationUserDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Adds a user to an organization.
     *
     * @param organizationId The ID of the organization
     * @param addUserRequest The request containing user to be added
     * @return The DTO representation of the added user.
     */
    public OrganizationUserDTO addUserToOrganization(
        // TODO ADD VALIDATE PERMISSIONS
        Long organizationId, OrganizationAddUserRequest addUserRequest) {
        // Get email for the new user from request object
        String userEmailToAdd = addUserRequest.email();

        // Get organization
        Organization organization = organizationUtil.getOrganizationById(organizationId);

        // Validate that an organization user does not already exist
        organizationUserUtil.validateOrganizationUserDoesNotExistsByEmail(userEmailToAdd, organizationId);

        // Validate user can add other users to organization
        organizationUtil.validateUserAuthorizationForOrganizationAction(
            OrganizationAction.ADD_USER, addUserRequest.organizationUserId(), organizationId);

        // Get User object for the new user
        User user = userUtil.getUserByEmail(userEmailToAdd);

        // Create an organization user for the new user
        OrganizationUser organizationUser = OrganizationUser.builder()
            .organization(organization)
            .user(user)
            .role(OrganizationRole.MEMBER)
            .build();

        // Save new organization user
        organizationUserRepository.save(organizationUser);
        return organizationUserDTOMapper.apply(organizationUser);
    }

    // TODO TEST

    /**
     * Remove a user from an organization.
     *
     * @param organizationId          The ID of the organization
     * @param organizationUserId      The ID of the organization user to be removed
     * @param userTryingToDeleteEmail The email of the user trying to remove another user
     * @return The ID of the organization user that was removed
     */
    public Long removeUserFromOrganization(Long organizationId, Long organizationUserId,
                                           String userTryingToDeleteEmail) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        // Get the organization user that is trying to remove user
        OrganizationUser userThatIsTryingToRemove = organizationUserUtil.getOrganizationUserByEmailAndOrganization(
            userTryingToDeleteEmail, organizationId);

        // Get the organization user that is to be removed
        OrganizationUser userToBeRemoved = organizationUserUtil.getOrganizationUserById(organizationUserId);

        // Validate organization user that is trying to remove has permissions
        validateUserPermissionsForUserRemoval(userToBeRemoved, userThatIsTryingToRemove);

        // The ID of the organization user to be removed
        Long userToBeRemovedId = userToBeRemoved.getId();

        // Delete ticket comments made by user to be removed
        ticketCommentRepository.deleteByPublisher_Id(userToBeRemovedId);

        // Delete tickets by user to be removed
        ticketRepository.deleteByPublisher_Id(userToBeRemovedId);

        // Finally delete organization user for user to be removed
        organizationUserRepository.deleteById(userToBeRemovedId);

        return userToBeRemovedId;
    }

    // TEST

    /**
     * Get the number of organizations associated with a specific email.
     *
     * @param userEmail the email to match
     * @return the count of organizations associated with the specified email
     */
    public Long getOrganizationCountForUser(String userEmail) {
        return organizationRepository.countByUsers_User_Email(userEmail);
    }

    /**
     * Validates the user permissions for removing an organization user. Checks if the user attempting the removal has
     * the necessary permissions and if the user being removed can be deleted.
     *
     * @param userToBeRemoved          The organization user to be removed.
     * @param userThatIsTryingToRemove The organization user attempting the removal.
     * @throws OrganizationUnauthorizedActionException If the user attempting the removal does not have the required
     *                                                 permissions.
     * @throws OrganizationException                   If any of the validation conditions are not met.
     */
    private void validateUserPermissionsForUserRemoval(OrganizationUser userToBeRemoved,
                                                       OrganizationUser userThatIsTryingToRemove) {
        Long userToBeRemovedId = userToBeRemoved.getId();

        // Check if they have the same organization
        boolean areUsersNotInSameOrg = !userThatIsTryingToRemove.getOrganization()
            .getId()
            .equals(userToBeRemoved.getOrganization()
                .getId());

        // You cannot delete an owner
        boolean isTryingToDeleteAnOwner = userToBeRemoved.getRole()
            .equals(OrganizationRole.OWNER);

        boolean isTryingToDeleteThemselves = userToBeRemovedId.equals(userThatIsTryingToRemove.getId());

        // You cannot delete someone with equal or higher rank/priority
        boolean hasLowRoles = userToBeRemoved.getRole()
            .hasGreaterOrEqualPriorityThan(userThatIsTryingToRemove.getRole());

        // The minimum role required to remove a user
        OrganizationRole minRole = OrganizationAction.REMOVE_USER.getRoleRequired();

        boolean hasPermission = userThatIsTryingToRemove.getRole()
            .hasGreaterOrEqualPriorityThan(minRole);

        if (!hasPermission) {
            throw new OrganizationUnauthorizedActionException(minRole);
        } else if (isTryingToDeleteAnOwner) {
            throw new OrganizationException("You cannot delete the owner");
        } else if (isTryingToDeleteThemselves) {
            throw new OrganizationException("You cannot delete yourself");
        } else if (hasLowRoles) {
            throw new OrganizationException("You cannot delete someone with the same role as you");
        } else if (areUsersNotInSameOrg) {
            throw new OrganizationException("You cannot delete this user");
        }
    }

}
