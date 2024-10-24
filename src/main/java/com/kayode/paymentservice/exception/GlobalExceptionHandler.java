package com.kayode.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.kayode.paymentservice.dto.CustomResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        CustomResponse<Object> response = new CustomResponse<>();
        response.setStatus(false);
        response.setMessage("Error occured");
        response.setData(errors);

        return ResponseEntity.badRequest().body(response);
    }

    // Handle missing or unreadable request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "Request body is missing or malformed";
        CustomResponse<Object> response = new CustomResponse<>();
        response.setStatus(false);
        response.setMessage(errorMessage);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CustomResponse<Object>> handleNoHandlerFound(NoHandlerFoundException ex) {
        CustomResponse<Object> response = new CustomResponse<>();
        response.setStatus(false);
        response.setMessage("The requested resource was not found");
        Map<String, String> details = new HashMap<>();
        details.put("path", ex.getRequestURL());
        details.put("method", ex.getHttpMethod());
        response.setData(details);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CustomResponse<Object>> handleAllUncaughtException(Exception ex) {
        CustomResponse<Object> response = new CustomResponse<>();
        response.setStatus(false);
        response.setMessage("An unexpected error occurred");
        
        // Only include detailed error message in non-production environments
        if (!"prod".equals(System.getProperty("spring.profiles.active"))) {
            response.setData(ex.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
