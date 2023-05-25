package com.jonathand.issuecrush.response;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;

public class APIResponseUtil {

    /**
     * Builds a success API response with the given result.
     *
     * @param result The result to be included in the response.
     * @param <T>    The type of the result.
     * @return An APIResponse object representing a successful response with the given result.
     */
    public <T> APIResponse<T> buildSuccessResponse(T result) {
        return APIResponse.<T>builder()
            .responseType(APIResponseType.SUCCESS)
            .result(result)
            .build();
    }

    /**
     * Creates an APIErrorDTO object based on the provided exception.
     *
     * @param exception The exception from which to create the APIErrorDTO.
     * @return The created APIErrorDTO object.
     */
    public APIErrorDTO createAPIErrorDTO(Exception exception) {
        return new APIErrorDTO(exception.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("Z")));
    }

    /**
     * Creates a List of APIErrorDTO object based on the provided exception.
     *
     * @param bindException The exception from which to create the APIErrorDTOs.
     * @return The created List of APIErrorDTOs object.
     */
    public List<APIErrorDTO> createAPIErrorDTOsForBindException(BindException bindException) {
        return bindException.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new APIErrorDTO(error.getDefaultMessage(), HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z"))))
            .collect(Collectors.toList());
    }

    /**
     * Creates an APIResponse object with the List of APIErrorDTO objects.
     *
     * @param errorDTOList The List of APIErrorDTOs to include in the APIResponse.
     * @return The created APIResponse object.
     */
    public APIResponse<APIErrorDTO> createAPIResponse(List<APIErrorDTO> errorDTOList) {
        APIResponse<APIErrorDTO> response = APIResponse.<APIErrorDTO>builder()
            .build();
        response.setResponseType(APIResponseType.FAILED);
        response.setErrors(errorDTOList);
        return response;
    }

    /**
     * Creates an APIResponse object with the APIErrorDTO object.
     *
     * @param errorDTO The APIErrorDTO to include in the APIResponse.
     * @return The created APIResponse object.
     */
    public APIResponse<APIErrorDTO> createAPIResponse(APIErrorDTO errorDTO) {
        APIResponse<APIErrorDTO> response = APIResponse.<APIErrorDTO>builder()
            .build();
        response.setResponseType(APIResponseType.FAILED);
        response.setErrors(Collections.singletonList(errorDTO));
        return response;
    }

}
