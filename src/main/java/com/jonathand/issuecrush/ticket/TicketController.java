package com.jonathand.issuecrush.ticket;

import java.security.Principal;
import java.util.List;

import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import com.jonathand.issuecrush.ticket.property.TicketPriority;
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
@RequestMapping("api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    private final APIResponseUtil apiResponseUtil;

    /**
     * Retrieves a paginated list of tickets for the specified organization.
     *
     * @param organizationId        the ID of the organization to retrieve tickets for
     * @param page                  the page number for pagination (default: 0)
     * @param size                  the number of tickets per page (default: 10)
     * @param title                 optional filter for ticket titles (default: null)
     * @param organizationUserId    optional filter for organization user ID (default: null)
     * @param sortByLatestCreatedAt flag to determine the sorting order of tickets by latest created date (default:
     *                              false)
     * @return a ResponseEntity containing the APIResponse with a list of TicketDTOs
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserMemberOfOrganization(#organizationId)")
    @GetMapping("organization/{organizationId}")
    public ResponseEntity<APIResponse<List<TicketDTO>>> getAllTicketsForOrganization(
        @PathVariable("organizationId") Long organizationId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "organizationUserId", required = false) Long organizationUserId,
        @RequestParam(value = "sortByLatestCreatedAt", defaultValue = "false") boolean sortByLatestCreatedAt) {
        // If sortByLatestCreatedAt, then sort by Ticket#createdAt
        Sort sortBy = sortByLatestCreatedAt ? Sort.by("createdAt")
            .descending() : Sort.unsorted();

        // Pagination information
        Pageable pageable = PageRequest.of(page, size, sortBy);

        // Retrieve all tickets for the organization. If filterByTitle,
        // search for ticket titles that match request parameter title.
        // If getForOrganizationUser then tickets will be sorted by publisher
        List<TicketDTO> ticketsForOrganization;
        boolean filterByTitle = title != null;
        boolean getForOrganizationUser = organizationUserId != null;

        if (filterByTitle && getForOrganizationUser) {
            ticketsForOrganization = ticketService.getAllTicketsForOrganizationByTitleAndByOrganizationUser(
                organizationId, organizationUserId, title, pageable);
        } else if (filterByTitle) {
            ticketsForOrganization = ticketService.getAllTicketsForOrganizationByTitle(organizationId, title, pageable);
        } else if (getForOrganizationUser) {
            ticketsForOrganization = ticketService.getAllTicketsForOrganizationByOrganizationUser(organizationId,
                organizationUserId, pageable);
        } else {
            ticketsForOrganization = ticketService.getAllTicketsForOrganization(organizationId, pageable);
        }

        // If tickets is empty, return no content response
        if (ticketsForOrganization.isEmpty()) {
            return ResponseEntity.noContent()
                .build();
        }

        // Build success response with list of tickets
        APIResponse<List<TicketDTO>> response = apiResponseUtil.buildSuccessResponse(ticketsForOrganization);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new ticket for the specified organization.
     *
     * @param organizationId      the ID of the organization to create the ticket for
     * @param ticketCreateRequest the TicketCreateRequest containing the ticket details
     * @param principal           the Principal representing the authenticated user
     * @return a ResponseEntity containing the APIResponse with the created TicketDTO
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserMemberOfOrganization(#organizationId)")
    @PostMapping("organization/{organizationId}")
    public ResponseEntity<APIResponse<TicketDTO>> createTicketForOrganization(
        @PathVariable("organizationId") Long organizationId,
        @Valid @RequestBody TicketCreateRequest ticketCreateRequest,
        Principal principal) {
        // Retrieve ticket
        TicketDTO ticketDTO = ticketService.createTicket(organizationId, principal.getName(), ticketCreateRequest);

        // Build success response with ticket
        APIResponse<TicketDTO> response = apiResponseUtil.buildSuccessResponse(ticketDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the details of the specified ticket.
     *
     * @param ticketId the ID of the ticket to retrieve
     * @return a ResponseEntity containing the APIResponse with the retrieved TicketDTO
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserInTicketOrganization(#ticketId)")
    @GetMapping("{ticketId}")
    public ResponseEntity<APIResponse<TicketDTO>> getTicket(@PathVariable("ticketId") Long ticketId) {
        // Retrieve ticket
        TicketDTO ticketDTO = ticketService.getTicket(ticketId);

        // Build success response with ticket
        APIResponse<TicketDTO> response = apiResponseUtil.buildSuccessResponse(ticketDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the specified ticket with the provided information.
     *
     * @param ticketId            the ID of the ticket to update
     * @param ticketUpdateRequest the TicketUpdateRequest containing the updated ticket information
     * @return a ResponseEntity containing the APIResponse with the updated TicketDTO
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserInTicketOrganization(#ticketId)")
    @PutMapping("{ticketId}")
    public ResponseEntity<APIResponse<TicketDTO>> updateTicket(
        @PathVariable("ticketId") Long ticketId,
        @Valid @RequestBody TicketUpdateRequest ticketUpdateRequest) {
        // Retrieve updated ticket
        TicketDTO updatedTicketDTO = ticketService.updateTicket(ticketId, ticketUpdateRequest);

        // Build success response with ticket
        APIResponse<TicketDTO> response = apiResponseUtil.buildSuccessResponse(updatedTicketDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes the specified ticket with the provided information.
     *
     * @param ticketId           the ID of the ticket to update
     * @param organizationUserId the ID of the user trying to delete
     * @return a ResponseEntity containing the APIResponse with the updated TicketDTO
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserInTicketOrganization(#ticketId)")
    @DeleteMapping("{ticketId}")
    public ResponseEntity<APIResponse<Long>> deleteTicket(
        @PathVariable("ticketId") Long ticketId,
        @RequestParam("organizationUserId") Long organizationUserId) {
        // Retrieve deleted ticket ID
        Long deletedTicketDTO = ticketService.deleteTicket(organizationUserId, ticketId);

        // Build success response with deleted ticket ID
        APIResponse<Long> response = apiResponseUtil.buildSuccessResponse(deletedTicketDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the list of ticket priorities.
     *
     * @return a ResponseEntity containing the APIResponse with the list of TicketPriority values
     */
    // TODO ADD TEST
    @GetMapping("/priorities")
    public ResponseEntity<APIResponse<List<TicketPriority>>> getTicketPriorities() {
        // Retrieve all ticket priorities
        List<TicketPriority> priorities = List.of(TicketPriority.values());

        // Build success response with all ticket priorities
        APIResponse<List<TicketPriority>> response = apiResponseUtil.buildSuccessResponse(priorities);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the list of ticket departments.
     *
     * @return a ResponseEntity containing the APIResponse with the list of TicketPriority values
     */
    // TODO ADD TEST
    @GetMapping("/departments")
    public ResponseEntity<APIResponse<List<TicketDepartment>>> getTicketDepartments() {
        // Retrieve all ticket departments
        List<TicketDepartment> departments = List.of(TicketDepartment.values());

        // Build success response with all ticket departments
        APIResponse<List<TicketDepartment>> response = apiResponseUtil.buildSuccessResponse(departments);
        return ResponseEntity.ok(response);
    }

}
