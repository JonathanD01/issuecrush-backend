package com.jonathand.issuecrush.ticket.comment;

import java.security.Principal;
import java.util.List;

import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ticket-comments")
public class TicketCommentController {

    private final TicketCommentService ticketCommentService;

    private final APIResponseUtil apiResponseUtil;

    /**
     * Retrieves the comments for an organization.
     *
     * @param organizationId        the ID of the organization
     * @param page                  the page number for pagination (default: 0)
     * @param size                  the page size for pagination (default: 10)
     * @param content               the optional filter, will filter by content (default: null)
     * @param organizationUserId    the optional ID of the organization user (default: null)
     * @param sortByLatestCreatedAt indicates whether to sort by latest ticket comment creation date (default: false)
     * @return the response entity containing the list of ticket comment DTOs
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserMemberOfOrganization(#organizationId)")
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<APIResponse<List<TicketCommentDTO>>> getCommentsForOrganization(
        @PathVariable("organizationId") Long organizationId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(value = "content", required = false) String content,
        @RequestParam(value = "organizationUserId", required = false) Long organizationUserId,
        @RequestParam(value = "sortByLatestCreatedAt", defaultValue = "false")
        boolean sortByLatestCreatedAt) {
        // If sortByLatestCreatedAt, then sort by TicketComment#createdAt
        Sort sortBy = sortByLatestCreatedAt
                      ? Sort.by("createdAt")
                          .descending()
                      : Sort.unsorted();

        // Pagination information
        Pageable pageable = PageRequest.of(page, size, sortBy);

        // Retrieve all comments for the organization. If filterByContent,
        // search for ticket comment where content contains the request param content
        // If getForOrganizationUser then tickets will be sorted by publisher
        boolean filterByContent = content != null;
        boolean getForOrganizationUser = organizationUserId != null;
        List<TicketCommentDTO> ticketCommentDTOS;
        if (filterByContent && getForOrganizationUser) {
            ticketCommentDTOS = ticketCommentService.getAllCommentsForOrganizationByCommentFilterAndByUser(
                organizationId, organizationUserId, content, pageable);
        } else if (filterByContent) {
            ticketCommentDTOS = ticketCommentService.getAllCommentsForOrganizationByCommentFilter(organizationId,
                content, pageable);
        } else if (getForOrganizationUser) {
            ticketCommentDTOS = ticketCommentService.getAllCommentsForOrganizationByUser(
                organizationId,
                organizationUserId,
                pageable);
        } else {
            ticketCommentDTOS = ticketCommentService.getAllCommentsForOrganization(organizationId, pageable);
        }

        // If ticket comments is empty, return no content
        if (ticketCommentDTOS.isEmpty()) {
            return ResponseEntity.noContent()
                .build();
        }

        // Build success response with list of ticket comments
        APIResponse<List<TicketCommentDTO>> response = apiResponseUtil.buildSuccessResponse(ticketCommentDTOS);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the comments for a specific ticket.
     *
     * @param ticketId              the ID of the ticket
     * @param page                  the page number for pagination (default: 0)
     * @param size                  the page size for pagination (default: 10)
     * @param content               the optional filter for comments (default: null)
     * @param sortByLatestCreatedAt indicates whether to sort by the latest ticket comment creation date (default:
     *                              false)
     * @return the response entity containing the list of ticket comment DTOs
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserInTicketOrganization(#ticketId)")
    @GetMapping("{ticketId}")
    public ResponseEntity<APIResponse<List<TicketCommentDTO>>> getTicketCommentsForTicket(
        @PathVariable("ticketId") Long ticketId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(value = "content", required = false) String content,
        @RequestParam(value = "sortByLatestCreatedAt", defaultValue = "false")
        boolean sortByLatestCreatedAt) {
        // If sortByLatestCreatedAt, then sort by TicketComment#createdAt
        Sort sortBy =
            sortByLatestCreatedAt
            ? Sort.by("createdAt")
                .descending()
            : Sort.unsorted();

        Pageable pageable = PageRequest.of(page, size, sortBy);

        // Retrieve all comments for ticket. If filterByContent,
        // search for ticket comment where content contains the request param content
        boolean filterByContent = content != null;
        List<TicketCommentDTO> ticketCommentDTOS =
            filterByContent
            ? ticketCommentService.getCommentsForTicketByComment(ticketId, content, pageable)
            : ticketCommentService.getCommentsForTicket(ticketId, pageable);

        // If ticket comments is empty, return no content
        if (ticketCommentDTOS.isEmpty()) {
            return ResponseEntity.noContent()
                .build();
        }

        // Build success response with list of ticket comments
        APIResponse<List<TicketCommentDTO>> response = apiResponseUtil.buildSuccessResponse(ticketCommentDTOS);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the count of comments for a specific ticket.
     *
     * @param ticketId the ID of the ticket
     * @return the response entity containing the count of ticket comments
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserInTicketOrganization(#ticketId)")
    @GetMapping("{ticketId}/count")
    public ResponseEntity<APIResponse<Long>> getTicketCommentsCount(
        @PathVariable("ticketId") Long ticketId) {
        // Retrieve ticket comment count for a specific ticket
        Long ticketCommentCount = ticketCommentService.getCommentCountForTicket(ticketId);

        // Build success response with count of ticket comments in ticket
        APIResponse<Long> response = apiResponseUtil.buildSuccessResponse(ticketCommentCount);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new ticket comment for a specific ticket.
     *
     * @param ticketId      the ID of the ticket
     * @param createRequest the request object containing the details of the ticket comment to be created
     * @param principal     the principal object representing the currently authenticated user
     * @return the response entity containing the created ticket comment
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserInTicketOrganization(#ticketId)")
    @PostMapping("{ticketId}")
    public ResponseEntity<APIResponse<TicketCommentDTO>> createCommentForTicket(
        @PathVariable("ticketId") Long ticketId,
        @Valid @RequestBody TicketCommentCreateRequest createRequest,
        Principal principal) {
        // Retrieve created ticket comment
        TicketCommentDTO ticketCommentDTO = ticketCommentService.createCommentForTicket(ticketId, createRequest,
            principal.getName());

        // Build success response with created ticket comment
        APIResponse<TicketCommentDTO> response = apiResponseUtil.buildSuccessResponse(ticketCommentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a specific ticket comment by its ID.
     *
     * @param ticketCommentId the ID of the ticket comment
     * @return the response entity containing the ticket comment
     */
    @PreAuthorize(
        "@organizationSecurityExpressions.isUserInTicketCommentOrganization(#ticketCommentId)")
    @GetMapping("/comment/{ticketCommentId}")
    public ResponseEntity<APIResponse<TicketCommentDTO>> getTicketComment(
        @PathVariable("ticketCommentId") Long ticketCommentId) {
        // Retrieve ticket comment
        TicketCommentDTO ticketCommentDTO = ticketCommentService.getTicketCommentFromId(ticketCommentId);

        // Build success response with ticket comment
        APIResponse<TicketCommentDTO> response = apiResponseUtil.buildSuccessResponse(ticketCommentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates a ticket comment with the specified ID.
     *
     * @param ticketCommentId the ID of the ticket comment to update
     * @param updateRequest   the request object containing the updated ticket comment details
     * @return the response entity containing the updated ticket comment
     */
    @PreAuthorize(
        "@organizationSecurityExpressions.isUserInTicketCommentOrganization(#ticketCommentId)")
    @PutMapping("comment/{ticketCommentId}")
    public ResponseEntity<APIResponse<TicketCommentDTO>> updateTicketComment(
        @PathVariable("ticketCommentId") Long ticketCommentId,
        @Valid @RequestBody TicketCommentUpdateRequest updateRequest) {
        // Retrieve updated ticket comment
        TicketCommentDTO ticketCommentDTO = ticketCommentService.updateTicketComment(ticketCommentId, updateRequest);

        // Build success response with updated ticket comment
        APIResponse<TicketCommentDTO> response = apiResponseUtil.buildSuccessResponse(ticketCommentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a ticket comment with the specified ID.
     *
     * @param ticketCommentId    the ID of the ticket comment to delete
     * @param organizationUserId the ID of the organization user trying to delete
     * @return the response entity containing the ID of the deleted ticket comment
     */
    @PreAuthorize(
        "@organizationSecurityExpressions.isUserInTicketCommentOrganization(#ticketCommentId)")
    @DeleteMapping("comment/{ticketCommentId}")
    public ResponseEntity<APIResponse<Long>> deleteTicketComment(
        @PathVariable("ticketCommentId") Long ticketCommentId,
        @RequestParam("organizationUserId") Long organizationUserId) {
        // Retrieve deleted ticket comment id
        Long deletedTicketCommentId = ticketCommentService.deleteTicketComment(ticketCommentId, organizationUserId);

        // Build success response with deleted ticket id
        APIResponse<Long> response = apiResponseUtil.buildSuccessResponse(deletedTicketCommentId);
        return ResponseEntity.ok(response);
    }

}
