package com.jonathand.issuecrush.organization;

import java.security.Principal;
import java.util.List;

import com.jonathand.issuecrush.organization.user.OrganizationUserDTO;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    private final APIResponseUtil apiResponseUtil;

    /**
     * Retrieves organizations for a user. If a non-blank organization name is provided, the organizations will be
     * filtered by name.
     *
     * @param organizationName the name of the organization (optional)
     * @param pageable         pagination information
     * @param principal        the authenticated user principal
     * @return a ResponseEntity containing the API response with a list of OrganizationDTO objects
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<OrganizationDTO>>> getOrganizationsForUser(
        @RequestParam(name = "name", required = false)
        String organizationName,
        Pageable pageable,
        Principal principal) {
        // Filter by organization name if present
        boolean filterByOrganizationName = organizationName != null;
        List<OrganizationDTO> organizations = filterByOrganizationName ?
                                              organizationService.getOrganizationsForUserByOrganizationName(
            organizationName, principal.getName(), pageable) : organizationService.getOrganizationsForUser(
            principal.getName(), pageable);

        // If organizations is empty, return no content response
        if (organizations.isEmpty()) {
            return ResponseEntity.noContent()
                .build();
        }

        // Build success response with the list of organizations
        APIResponse<List<OrganizationDTO>> response = apiResponseUtil.buildSuccessResponse(organizations);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the count of organizations for a user.
     *
     * @param principal the authenticated user principal
     * @return a ResponseEntity containing the API response with the organization count
     */
    @GetMapping("count")
    public ResponseEntity<APIResponse<Long>> getOrganizationCountForUser(Principal principal) {
        // Retrieve the count of organizations for the user
        Long organizationCount = organizationService.getOrganizationCountForUser(principal.getName());

        // Build success response with the organization count
        APIResponse<Long> response = apiResponseUtil.buildSuccessResponse(organizationCount);
        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new organization.
     *
     * @param request   the request body containing the new organization details
     * @param principal the authenticated user principal
     * @return a ResponseEntity containing the API response with the created OrganizationDTO object
     */
    @PostMapping
    public ResponseEntity<APIResponse<OrganizationDTO>> registerNewOrganization(
        @Valid @RequestBody OrganizationNewRequest request,
        Principal principal) {
        // Create a new organization
        OrganizationDTO createdOrganization = organizationService.addNewOrganization(principal.getName(), request);

        // Build success response with the created organization
        APIResponse<OrganizationDTO> response = apiResponseUtil.buildSuccessResponse(createdOrganization);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing organization.
     *
     * @param organizationId            the ID of the organization to update
     * @param organizationUpdateRequest the request body containing the updated organization details
     * @return a ResponseEntity containing the API response with the updated OrganizationDTO object
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserOrganizationOwner(#organizationId)")
    @PutMapping("{organizationId}")
    public ResponseEntity<APIResponse<OrganizationDTO>> updateOrganization(
        @PathVariable("organizationId") Long organizationId,
        @Valid @RequestBody OrganizationUpdateRequest organizationUpdateRequest) {
        // Update the organization
        OrganizationDTO organizationToUpdate = organizationService.updateOrganization(organizationId,
            organizationUpdateRequest);

        // Build success response with the updated organization
        APIResponse<OrganizationDTO> response = apiResponseUtil.buildSuccessResponse(organizationToUpdate);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an organization.
     *
     * @param organizationId the ID of the organization to delete
     * @return a ResponseEntity containing the API response with the deleted organization ID
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserOrganizationOwner(#organizationId)")
    @DeleteMapping("{organizationId}")
    public ResponseEntity<APIResponse<Long>> deleteOrganization(@PathVariable("organizationId") Long organizationId) {
        // Delete the organization
        Long deletedOrganizationId = organizationService.deleteOrganization(organizationId);

        // Build success response with the deleted organization ID
        APIResponse<Long> response = apiResponseUtil.buildSuccessResponse(deletedOrganizationId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves an organization by ID.
     *
     * @param organizationId the ID of the organization to retrieve
     * @return a ResponseEntity containing the API response with the retrieved OrganizationDTO object
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserMemberOfOrganization(#organizationId)")
    @GetMapping("{organizationId}")
    public ResponseEntity<APIResponse<OrganizationDTO>> getOrganization(
        @PathVariable("organizationId") Long organizationId) {
        // Retrieve organization from ID
        OrganizationDTO organizationDTO = organizationService.getOrganization(organizationId);

        // Build success response with the retrieved organization
        APIResponse<OrganizationDTO> response = apiResponseUtil.buildSuccessResponse(organizationDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves statistics for an organization.
     *
     * @param organizationId the ID of the organization
     * @return a ResponseEntity containing the API response with the OrganizationStatisticsDTO object
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserMemberOfOrganization(#organizationId)")
    @GetMapping("{organizationId}/statistics")
    public ResponseEntity<APIResponse<OrganizationStatisticsDTO>> getOrganizationStatistics(
        @PathVariable("organizationId") Long organizationId) {
        // Retrieve an organization's statistics from ID
        OrganizationStatisticsDTO organizationStatisticsDTO = organizationService.getOrganizationStatistics(
            organizationId);

        // Build success response with organization's statistics
        APIResponse<OrganizationStatisticsDTO> response = apiResponseUtil.buildSuccessResponse(
            organizationStatisticsDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves users of an organization.
     *
     * @param organizationId the ID of the organization
     * @param page           the page number for pagination
     * @param size           the page size for pagination
     * @param email          the email filter for users (optional)
     * @return a ResponseEntity containing the API response with a list of OrganizationUserDTO objects
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserMemberOfOrganization(#organizationId)")
    @GetMapping("{organizationId}/users")
    public ResponseEntity<APIResponse<List<OrganizationUserDTO>>> getOrganizationUsers(
        @PathVariable("organizationId") Long organizationId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(value = "email", required = false) String email) {
        Pageable pageable = PageRequest.of(page, size);

        boolean filterByEmail = email != null;

        List<OrganizationUserDTO> organizationUserDTOS = filterByEmail ?
                                                         organizationService.getOrganizationUsersByEmail(
            organizationId, email, pageable) : organizationService.getOrganizationUsers(organizationId, pageable);


        // Build success response with the list of organization users
        APIResponse<List<OrganizationUserDTO>> response = apiResponseUtil.buildSuccessResponse(organizationUserDTOS);
        return ResponseEntity.ok(response);
    }

    /**
     * Adds a user to an organization.
     *
     * @param organizationId the ID of the organization
     * @param addUserRequest the request body containing the user details to add
     * @return a ResponseEntity containing the API response with the added OrganizationUserDTO object
     */
    @PreAuthorize("@organizationSecurityExpressions.canUserAddOrganizationUser(#organizationId)")
    @PostMapping("{organizationId}/users")
    public ResponseEntity<APIResponse<OrganizationUserDTO>> addUserToOrganization(
        @PathVariable("organizationId") Long organizationId,
        @Valid @RequestBody OrganizationAddUserRequest addUserRequest) {
        // Add a user to the organization
        OrganizationUserDTO organizationUserDTO = organizationService.addUserToOrganization(organizationId,
            addUserRequest);

        // Build success response with the added organization user
        APIResponse<OrganizationUserDTO> response = apiResponseUtil.buildSuccessResponse(organizationUserDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Removes a user from an organization.
     *
     * @param organizationId     the ID of the organization
     * @param organizationUserId the ID of the user to remove
     * @return a ResponseEntity containing the API response with the removed user ID
     */
    @DeleteMapping("{organizationId}/users/{organizationUserId}")
    public ResponseEntity<APIResponse<Long>> removeUserFromOrganization(
        @PathVariable("organizationId") Long organizationId,
        @PathVariable("organizationUserId") Long organizationUserId,
        Principal principal) {
        // Remove a user from the organization
        Long deletedOrganizationUserId = organizationService.removeUserFromOrganization(organizationId,
            organizationUserId, principal.getName());

        // Build success response with deleted organization ID
        APIResponse<Long> response = apiResponseUtil.buildSuccessResponse(deletedOrganizationUserId);
        return ResponseEntity.ok(response);
    }

}
