package com.jonathand.issuecrush.organization;

import com.jonathand.issuecrush.organization.user.OrganizationUserAlreadyExistsException;
import com.jonathand.issuecrush.organization.user.OrganizationUserNotFoundException;
import com.jonathand.issuecrush.response.APIErrorDTO;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class OrganizationExceptionHandler {

    private final APIResponseUtil apiResponseUtil;

    /**
     * Handles the OrganizationException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The OrganizationException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = OrganizationException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleOrganizationException(OrganizationException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

    /**
     * Handles the OrganizationRequestException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The OrganizationRequestException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = OrganizationRequestException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleOrganizationRequestException(
        OrganizationRequestException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

    /**
     * Handles the OrganizationNotFoundException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The OrganizationNotFoundException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = OrganizationNotFoundException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleOrganizationNotFoundException(
        OrganizationNotFoundException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

    /**
     * Handles the OrganizationUserNotFoundException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The OrganizationUserNotFoundException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = OrganizationUserNotFoundException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleOrganizationUserNotFoundException(
        OrganizationUserNotFoundException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

    /**
     * Handles the OrganizationUserAlreadyExistsException and returns a ResponseEntity with the appropriate API
     * response.
     *
     * @param exception The OrganizationUserAlreadyExistsException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = OrganizationUserAlreadyExistsException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleOrganizationUserAlreadyExistsException(
        OrganizationUserAlreadyExistsException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

    /**
     * Handles the OrganizationUnauthorizedActionException and returns a ResponseEntity with the appropriate API
     * response.
     *
     * @param exception The OrganizationUnauthorizedActionException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = OrganizationUnauthorizedActionException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleOrganizationUnauthorizedActionException(
        OrganizationUnauthorizedActionException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

    /**
     * Handles the OrganizationRoleNotFoundException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The OrganizationRoleNotFoundException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = OrganizationRoleNotFoundException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleOrganizationRoleNotFoundException(
        OrganizationRoleNotFoundException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

}
