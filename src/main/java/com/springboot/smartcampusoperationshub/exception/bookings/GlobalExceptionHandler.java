package com.springboot.smartcampusoperationshub.exception.bookings;

import com.springboot.smartcampusoperationshub.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BookingNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleBookingNotFound(
          BookingNotFoundException exception, HttpServletRequest request
  ) {
    return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
  }

  @ExceptionHandler(BookingConflictException.class)
  public ResponseEntity<ErrorResponseDTO> handleBookingConflict(
          BookingConflictException exception, HttpServletRequest request
  ) {
    return buildError(HttpStatus.CONFLICT, exception.getMessage(), request);
  }

  @ExceptionHandler(InvalidStatusTransitionException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidTransition(
          InvalidStatusTransitionException exception, HttpServletRequest request) {
    return buildError(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(), request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
          IllegalArgumentException exception, HttpServletRequest request) {
    return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidation(
          MethodArgumentNotValidException exception, HttpServletRequest request) {

    Map<String, String> validationErrors = new LinkedHashMap<>();
    exception.getBindingResult().getFieldErrors().forEach(fieldError ->
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
    );

    ErrorResponseDTO body = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "One or more fields failed validation",
            request.getRequestURI(),
            validationErrors
    );

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneric(
          Exception exception, HttpServletRequest request) {
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred", request);
  }

  private ResponseEntity<ErrorResponseDTO> buildError(HttpStatus status,  String message, HttpServletRequest request) {
    ErrorResponseDTO body = new ErrorResponseDTO(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI(),
            null
    );

    return ResponseEntity.status(status).body(body);
  }
}