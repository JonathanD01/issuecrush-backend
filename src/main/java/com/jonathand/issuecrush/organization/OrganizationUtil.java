package com.jonathand.issuecrush.organization;

import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public class OrganizationUtil {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationUserUtil organizationUserUtil;

    /**
     * Retrieves an organization by its ID.
     *
     * @param organizationId the ID of the organization to retrieve
     * @return the Organization object with the specified ID
     * @throws OrganizationNotFoundException if the organization is not found
     */
    public Organization getOrganizationById(Long organizationId) {
        return organizationRepository.findById(organizationId)
            .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
    }

    /**
     * Validates if an organization with specified ID exists in the system.
     *
     * @param organizationId The ID of the organization to validate
     * @throws OrganizationNotFoundException If the user with the specified email is not found.
     */
    public void validateOrganizationExistsById(Long organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new OrganizationNotFoundException(organizationId);
        }
    }

    // TODO TEST

    /**
     * Validates whether the user has authorization to perform an action on the organization.
     *
     * @param organizationAction the action user is performing
     * @param organizationUserId the ID of the organization user
     * @param organizationId     the ID of organization
     * @throws OrganizationUnauthorizedActionException if the user is not authorized to perform the action
     */
    public void validateUserAuthorizationForOrganizationAction(OrganizationAction organizationAction,
                                                               Long organizationUserId, Long organizationId) {
        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserById(organizationUserId);
        Organization organization = getOrganizationById(organizationId);

        OrganizationRole roleRequired = organizationAction.getRoleRequired();

        boolean hasPermission = organizationUser.getRole()
            .hasGreaterOrEqualPriorityThan(roleRequired);

        boolean isCreator = organization.getCreator()
            .getId()
            .equals(organizationUser.getUser()
                .getId());

        // Cannot remove owners
        if (organizationAction.equals(OrganizationAction.REMOVE_USER) && isCreator) {
            throw new OrganizationUnauthorizedActionException("You cannot remove the owner");
        }

        if (!hasPermission && !isCreator) {
            throw new OrganizationUnauthorizedActionException(roleRequired);
        }
    }

}
