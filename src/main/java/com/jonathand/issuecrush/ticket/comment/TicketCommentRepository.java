package com.jonathand.issuecrush.ticket.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    /**
     * Deletes all ticket comments published by a user with the specified ID.
     *
     * @param id the ID of the publisher
     */
    void deleteByPublisher_Id(Long id);

    /**
     * Retrieves a page of ticket comments for a specific organization filtered by ticket comment content.
     *
     * @param id       the ID of the organization
     * @param content  the content filter
     * @param pageable pagination information
     * @return a page of ticket comments matching the specified organization and ticket comment content filter
     */
    Page<TicketComment> findByPublisher_Organization_IdAndContentContainsIgnoreCase(Long id, String content,
                                                                                    Pageable pageable);

    /**
     * Retrieves a page of ticket comments for a specific organization and publisher filtered by ticket comment content.
     *
     * @param id       the ID of the organization
     * @param id1      the ID of the publisher
     * @param content  the content filter
     * @param pageable pagination information
     * @return a page of ticket comments matching the specified organization, publisher, and ticket comment content
     * filter
     */
    Page<TicketComment> findByPublisher_Organization_IdAndPublisher_IdAndContentContainsIgnoreCase(Long id, Long id1,
                                                                                                   String content,
                                                                                                   Pageable pageable);


    /**
     * Retrieves a page of ticket comments for a specific organization.
     *
     * @param id       the ID of the organization
     * @param pageable pagination information
     * @return a page of ticket comments for the specified organization
     */
    Page<TicketComment> findByPublisher_Organization_Id(Long id, Pageable pageable);


    /**
     * Counts the number of ticket comments published by users of a specific organization.
     *
     * @param id the ID of the organization
     * @return the number of ticket comments published by users of the specified organization
     */
    long countByPublisher_Organization_Id(Long id);

    /**
     * Retrieves a page of ticket comments for a specific ticket filtered by ticket comment content.
     *
     * @param ticketId the ID of the ticket
     * @param content  the content filter
     * @param pageable pagination information
     * @return a page of ticket comments matching the specified ticket and content filter
     */
    Page<TicketComment> findByTicketIdAndContentContainsIgnoreCase(
        Long ticketId, String content, Pageable pageable);

    /**
     * Counts the number of ticket comments published by a user with the specified ID.
     *
     * @param id the ID of the publisher
     * @return the number of ticket comments published by the user
     */
    long countByPublisher_Id(Long id);

    /**
     * Retrieves a page of ticket comments published by a user with the specified ID.
     *
     * @param id       the ID of the publisher
     * @param pageable pagination information
     * @return a page of ticket comments published by the user
     */
    Page<TicketComment> findByPublisher_Id(Long id, Pageable pageable);

    /**
     * Counts the number of ticket comments for a specific ticket.
     *
     * @param id the ID of the ticket
     * @return the number of ticket comments for the specified ticket
     */
    long countByTicketId(Long id);

    /**
     * Retrieves a page of ticket comments for a specific ticket.
     *
     * @param ticketId the ID of the ticket
     * @param pageable pagination information
     * @return a page of ticket comments for the specified ticket
     */
    @Query("SELECT tc " + "FROM TicketComment tc " + "WHERE tc.ticket.id = :ticketId")
    Page<TicketComment> getCommentsForTicketId(@Param("ticketId") Long ticketId, Pageable pageable);

    /**
     * Checks whether a user belongs to the organization associated with a ticket content.
     *
     * @param email           the email of the user
     * @param ticketCommentId the ID of the ticket content
     * @return true if the user belongs to the organization, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(ou) > 0 THEN true ELSE false END "
        + "FROM OrganizationUser ou "
        + "INNER JOIN ou.organization o "
        + "INNER JOIN o.tickets t "
        + "INNER JOIN t.comments tc "
        + "WHERE ou.user.email = :email "
        + "AND tc.id = :ticketCommentId")
    boolean doesUserBelongToTicketCommentOrganization(
        @Param("email") String email, @Param("ticketCommentId") Long ticketCommentId);

}
