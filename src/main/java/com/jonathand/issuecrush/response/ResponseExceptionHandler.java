package com.jonathand.issuecrush.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class ResponseExceptionHandler {

    private final APIResponseUtil apiResponseUtil;

    /**
     * Handles the ResponseMissingParameterException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The ResponseMissingParameterException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = ResponseMissingParameterException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleResponseMissingParameterException(
        ResponseMissingParameterException exception) {
        APIErrorDTO errorDTO = apiResponseUtil.createAPIErrorDTO(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest()
            .body(response);
    }

}
