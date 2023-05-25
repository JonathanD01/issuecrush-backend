package com.jonathand.issuecrush.organization.user;

import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/organization-users")
public class OrganizationUserController {

    private final OrganizationUserService organizationUserService;

    private final APIResponseUtil apiResponseUtil;

    /**
     * Retrieves the details of an organization user based on the organization ID, email, or organization user ID. If an
     * email is provided, the organization user will be filtered by email. However, if organization user ID is provided,
     * the organization user will be found using the ID and not email.
     *
     * @param organizationId     the ID of the organization.
     * @param userEmail          the email of the organization user (optional).
     * @param organizationUserId the ID of the organization user (optional).
     * @return ResponseEntity containing the API response with the organization user details.
     */
    // TODO TEST
    @PreAuthorize("@organizationSecurityExpressions.isUserMemberOfOrganization(#organizationId)")
    @GetMapping
    public ResponseEntity<APIResponse<OrganizationUserDTO>> getOrganizationUser(
        @RequestParam(name = "organizationId") Long organizationId,
        @RequestParam(name = "email", required = false) String userEmail,
        @RequestParam(name = "organizationUserId", required = false) Long organizationUserId) {
        // Whether to filter by email
        boolean filterByUserEmail = userEmail != null;

        // Retrieve organization user based on 'filterByUserEmail'
        OrganizationUserDTO organizationUserDTO =
            filterByUserEmail
            ? organizationUserService.getOrganizationUser(organizationId, userEmail)
            : organizationUserService.getOrganizationUser(organizationId, organizationUserId);

        // Build success response with organizationUserDTO
        APIResponse<OrganizationUserDTO> response =
            apiResponseUtil.buildSuccessResponse(organizationUserDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an organization user.
     *
     * @param userUpdateRequest the request body containing the new user details
     * @return ResponseEntity containing the API response with the updated organization user.
     */
    @PreAuthorize(
        "@organizationSecurityExpressions.isUserMemberOfOrganization(#userUpdateRequest.organizationId)")
    @PutMapping
    public ResponseEntity<APIResponse<OrganizationUserDTO>> updateOrganizationUser(
        @Valid @RequestBody OrganizationUserUpdateRequest userUpdateRequest) {
        // Retrieve organization user
        OrganizationUserDTO organizationUserDTO =
            organizationUserService.updateOrganizationUser(userUpdateRequest);

        // Build success response with updated organization user
        APIResponse<OrganizationUserDTO> response =
            apiResponseUtil.buildSuccessResponse(organizationUserDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the statistics for an organization user.
     *
     * @param organizationId     the ID of the organization.
     * @param organizationUserId the ID of the organization user.
     * @return ResponseEntity containing the API response with the organization user statistics.
     */
    @PreAuthorize("@organizationSecurityExpressions.isUserMemberOfOrganization(#organizationId)")
    @GetMapping("statistics")
    public ResponseEntity<APIResponse<OrganizationUserStatisticsDTO>> getOrganizationUserStatistics(
        @RequestParam(value = "organizationId") Long organizationId,
        @RequestParam(value = "organizationUserId") Long organizationUserId) {
        // Retrieve statistics for the organization user
        OrganizationUserStatisticsDTO organizationUserStatisticsDTO =
            organizationUserService.getOrganizationUserStatistics(organizationId, organizationUserId);

        // Build success response with the statistics for the organization user
        APIResponse<OrganizationUserStatisticsDTO> response =
            apiResponseUtil.buildSuccessResponse(organizationUserStatisticsDTO);
        return ResponseEntity.ok(response);
    }

}
