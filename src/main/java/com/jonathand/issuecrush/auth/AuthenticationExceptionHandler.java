package com.jonathand.issuecrush.auth;

import com.jonathand.issuecrush.response.APIErrorDTO;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class AuthenticationExceptionHandler {

    private final APIResponseUtil apiResponseUtil;

    /**
     * Handles the AccessDeniedException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The AccessDeniedException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleAccessDeniedException(AccessDeniedException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

    /**
     * Handles the EmailAlreadyTakenException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The EmailAlreadyTakenException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = EmailAlreadyTakenException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleEmailAlreadyTakenException(
        EmailAlreadyTakenException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

}
