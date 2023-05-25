package com.jonathand.issuecrush.auth;

import java.util.List;

import com.jonathand.issuecrush.response.APIErrorDTO;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class MethodArgumentNotValidExceptionHandler {

    private final APIResponseUtil apiResponseUtil;

    /**
     * Handles the MethodArgumentNotValidException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The MethodArgumentNotValidException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException exception) {
        List<APIErrorDTO> errorDTOList = apiResponseUtil.createAPIErrorDTOsForBindException(exception);
        APIResponse<APIErrorDTO> response = apiResponseUtil.createAPIResponse(errorDTOList);
        return ResponseEntity.badRequest()
            .body(response);
    }

}
