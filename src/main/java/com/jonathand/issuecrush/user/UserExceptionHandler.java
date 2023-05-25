package com.jonathand.issuecrush.user;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.jonathand.issuecrush.response.APIErrorDTO;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    /**
     * Handles the UsernameNotFoundException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The UsernameNotFoundException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleUsernameNotFoundException(
        UsernameNotFoundException exception) {
        APIResponse<APIErrorDTO> response = APIResponse.<APIErrorDTO>builder()
                                                       .build();

        APIErrorDTO errorDTO = new APIErrorDTO(exception.getMessage(), HttpStatus.BAD_REQUEST,
                                               ZonedDateTime.now(ZoneId.of("Z")));

        response.setResponseType(APIResponseType.FAILED);
        response.setErrors(List.of(errorDTO));
        return ResponseEntity.badRequest()
                             .body(response);
    }

}
