package com.jonathand.issuecrush.organization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    /**
     * Counts the number of organizations associated with a specific email.
     *
     * @param userEmail the email to match
     * @return the count of organizations associated with the specified email
     */
    long countByUsers_User_Email(String userEmail);

    /**
     * Retrieves a page of organizations associated with a specific email and matching the provided organization name.
     *
     * @param userEmail the email to match
     * @param name      the name of the organization to search for (case-insensitive)
     * @param pageable  pagination information
     * @return a page of organizations that match the specified email and contain the provided name
     */
    Page<Organization> findByUsers_User_EmailAndNameContainsIgnoreCase(
        String userEmail, String name, Pageable pageable);

    /**
     * Retrieves a page of organizations associated with a specific email.
     *
     * @param userEmail the email to match
     * @param pageable  pagination information
     * @return a page of organizations that are associated with the specified email
     */
    @Query("SELECT ou.organization "
           + "FROM OrganizationUser ou "
           + "WHERE ou.user.email = :userEmail")
    Page<Organization> findOrganizationsForEmail(
        @Param("userEmail") String userEmail, Pageable pageable);

}
