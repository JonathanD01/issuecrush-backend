package com.jonathand.issuecrush.organization.user;

import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationUserUtil {

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    /**
     * Retrieves an organization user by email and organization ID.
     *
     * @param userEmail      the email of the organization user
     * @param organizationId the ID of the organization
     * @return the OrganizationUser object matching the email and organization ID
     * @throws OrganizationUserNotFoundException if the organization user is not found
     */
    public OrganizationUser getOrganizationUserByEmailAndOrganization(String userEmail, Long organizationId) {
        return organizationUserRepository.findOrganizationUserByEmailAndOrganization(userEmail, organizationId)
            .orElseThrow(OrganizationUserNotFoundException::new);
    }

    /**
     * Retrieves an organization user by organization user ID and organization ID.
     *
     * @param organizationUserId The ID of the organization user
     * @return the OrganizationUser object matching the email and organization ID
     * @throws OrganizationUserNotFoundException if the organization user is not found
     */
    public OrganizationUser getOrganizationUserById(Long organizationUserId) {
        return organizationUserRepository.findById(organizationUserId)
            .orElseThrow(OrganizationUserNotFoundException::new);
    }

    /**
     * Validates if an organization user specified id does exist.
     *
     * @param organizationUserId The ID of the organization user
     * @throws OrganizationUserNotFoundException If an organization user with the specified ID is not found in an
     *                                           organization.
     */
    public void validateOrganizationUserDoesExists(Long organizationUserId) {
        boolean doesOrganizationUserExist = organizationUserRepository.existsById(organizationUserId);

        if (!doesOrganizationUserExist) {
            throw new OrganizationUserNotFoundException();
        }
    }

    /**
     * Validates if an organization user specified id does not exist.
     *
     * @param organizationUserId The ID of the organization user
     * @throws OrganizationUserAlreadyExistsException If an organization user with the specified email is found in
     *                                                an organization.
     */
    public void validateOrganizationUserDoesNotExists(Long organizationUserId) {
        boolean doesOrganizationUserExist = organizationUserRepository.existsById(organizationUserId);

        if (doesOrganizationUserExist) {
            throw new OrganizationUserAlreadyExistsException();
        }
    }

    /**
     * Validates if an organization user specified id and email does not exist.
     *
     * @param userEmail          the email of the user who is to be added
     * @param organizationUserId The ID of the organization user
     * @throws OrganizationUserAlreadyExistsException If an organization user with the specified email is found in
     *                                                an organization.
     */
    public void validateOrganizationUserDoesNotExistsByEmail(String userEmail, Long organizationUserId) {
        boolean doesOrganizationUserExist = organizationUserRepository.findOrganizationUserByEmailAndOrganization(
                userEmail, organizationUserId)
            .isPresent();

        if (doesOrganizationUserExist) {
            throw new OrganizationUserAlreadyExistsException();
        }
    }

}
