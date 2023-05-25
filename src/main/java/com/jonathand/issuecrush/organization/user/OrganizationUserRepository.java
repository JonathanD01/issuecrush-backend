package com.jonathand.issuecrush.organization.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {

    /**
     * Retrieves a page of organization users based on the organization ID and matching email.
     *
     * @param id       the ID of the organization to match
     * @param email    the email to match (case-insensitive)
     * @param pageable pagination information
     * @return a page of organization user objects that belong to the specified organization ID and have an email
     * containing the provided email parameter.
     */
    Page<OrganizationUser> findByOrganization_IdAndUser_EmailContainsIgnoreCase(
        Long id, String email, Pageable pageable);

    /**
     * Retrieves a page of organization users based on the organization ID.
     *
     * @param id       the ID of the organization to match
     * @param pageable pagination information
     * @return a page of organization user objects that belong to the specified organization ID.
     */
    Page<OrganizationUser> findByOrganization_Id(Long id, Pageable pageable);

    /**
     * Counts the number of organization users in the organization with the specified ID.
     *
     * @param id the ID of the organization to match
     * @return the count of organization users in the specified organization
     */
    long countByOrganization_Id(Long id);

    /**
     * Retrieves an organization user by email and organization ID.
     *
     * @param email          the email of the organization user to match
     * @param organizationId the ID of the organization to match
     * @return an Optional containing the organization user if found, or empty if not found
     */
    @Query("SELECT ou FROM OrganizationUser ou "
           + "WHERE ou.user.email = :email AND ou.organization.id = :organizationId")
    Optional<OrganizationUser> findOrganizationUserByEmailAndOrganization(
        @Param("email") String email, @Param("organizationId") Long organizationId);

}
