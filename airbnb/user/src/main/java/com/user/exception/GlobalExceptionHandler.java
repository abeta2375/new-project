package com.user.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e, WebRequest webRequest){
        logger.error("User already exists:{} "+ e.getMessage(),e);
        return new ResponseEntity<>("User already exists:{} "+ e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserListRetrievalException.class)
    public ResponseEntity<String> handleUserListRetrievalException(UserListRetrievalException e) {
        logger.error("Failed to retrieve users list: {}", e.getMessage(), e);
        return new ResponseEntity<>("Failed to retrieve users list: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(Exception e, WebRequest webRequest){
        logger.error("User not found:{}"+ e.getMessage(), e);
        return new ResponseEntity<>("User not found:{}"+ e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e, WebRequest webRequest) {
        logger.error("Validation error: {}", e.getMessage(), e);
        return new ResponseEntity<>("Validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception e, WebRequest webRequest){
        logger.error("An error occurred: "+e.getMessage(), e);
        return new ResponseEntity<>("Something went wrong.", HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
