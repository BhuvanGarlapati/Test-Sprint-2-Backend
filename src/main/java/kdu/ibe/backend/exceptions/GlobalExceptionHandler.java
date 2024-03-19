package kdu.ibe.backend.exceptions;

import kdu.ibe.backend.dto.response.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles BadRequestException by returning a ResponseEntity with an ErrorDTO containing a message
     * indicating an invalid request and the corresponding HTTP status code HttpStatus.BAD_REQUEST.
     *
     * @param ex The BadRequestException instance to be handled.
     * @return ResponseEntity containing the ErrorDTO with the error message and HTTP status code.
     */
    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<ErrorDTO> badRequestException(Exception ex){
        log.error("badRequestException: " + ex.getMessage());
        ErrorDTO error = new ErrorDTO("Invalid request: " + ex.getMessage() , HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions by returning a ResponseEntity with an ErrorDTO containing a generic
     * error message and the corresponding HTTP status code HttpStatus.NOT_FOUND.
     *
     * @param ex The Exception instance to be handled.
     * @return ResponseEntity containing the ErrorDTO with the generic error message and HTTP status code.
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorDTO> allKindOfExceptions(Exception ex){
        log.error("allKindOfExceptions: " + ex.getMessage());
        ErrorDTO error = new ErrorDTO("Invalid Request: " + ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}