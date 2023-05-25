package com.jonathand.issuecrush.ticket;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.jonathand.issuecrush.response.APIErrorDTO;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TicketExceptionHandler {

    /**
     * Handles the TicketNotFoundException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The TicketNotFoundException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = TicketNotFoundException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleTicketNotFoundException(TicketNotFoundException exception) {
        APIResponse<APIErrorDTO> response = APIResponse.<APIErrorDTO>builder()
            .build();

        APIErrorDTO errorDTO = new APIErrorDTO(exception.getMessage(), HttpStatus.BAD_REQUEST,
            ZonedDateTime.now(ZoneId.of("Z")));

        response.setResponseType(APIResponseType.FAILED);
        response.setErrors(List.of(errorDTO));
        return ResponseEntity.badRequest()
            .body(response);
    }

    /**
     * Handles the TicketUnauthorizedActionException and returns a ResponseEntity with the appropriate API response.
     *
     * @param exception The TicketUnauthorizedActionException that occurred.
     * @return The ResponseEntity containing the API response with the error details.
     */
    @ExceptionHandler(value = TicketUnauthorizedActionException.class)
    public ResponseEntity<APIResponse<APIErrorDTO>> handleTicketUnauthorizedActionException(
        TicketUnauthorizedActionException exception) {
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
