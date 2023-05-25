package com.jonathand.issuecrush.ticket.property;

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
@RequestMapping("api/v1/ticket-property")
public class TicketPropertyController {

    private final TicketPropertyService ticketPropertyService;

    private final APIResponseUtil apiResponseUtil;


    /**
     * Retrieves the property of the specified ticket.
     *
     * @param ticketId the ID of the ticket to retrieve the body from
     * @return a ResponseEntity containing the APIResponse with the TicketPropertyDTO
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserInTicketOrganization(#ticketId)")
    @GetMapping("{ticketId}")
    public ResponseEntity<APIResponse<TicketPropertyDTO>> getTicketProperty(
        @PathVariable("ticketId") Long ticketId) {
        // Retrieve ticket property
        TicketPropertyDTO ticketPropertyDTO = ticketPropertyService.getTicketPropertyForTicket(ticketId);

        // Build success response with ticket property
        APIResponse<TicketPropertyDTO> response =
            apiResponseUtil.buildSuccessResponse(ticketPropertyDTO);
        return ResponseEntity.ok(response);
    }

}

