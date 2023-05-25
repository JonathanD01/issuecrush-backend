package com.jonathand.issuecrush.ticket.body;

import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ticket-body")
public class TicketBodyController {

    private final TicketBodyService ticketBodyService;

    private final APIResponseUtil apiResponseUtil;


    /**
     * Retrieves the body of the specified ticket.
     *
     * @param ticketId the ID of the ticket to retrieve the body from
     * @return a ResponseEntity containing the APIResponse with the TicketBodyDTO
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserInTicketOrganization(#ticketId)")
    @GetMapping("{ticketId}")
    public ResponseEntity<APIResponse<TicketBodyDTO>> getTicketBody(
        @PathVariable("ticketId") Long ticketId) {
        // Retrieve ticket body
        TicketBodyDTO ticketBodyDTO = ticketBodyService.getTicketBodyForTicket(ticketId);

        // Build success response with ticket body
        APIResponse<TicketBodyDTO> response = apiResponseUtil.buildSuccessResponse(ticketBodyDTO);
        return ResponseEntity.ok(response);
    }

}

