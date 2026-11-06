package com.noctiva.noxway.shared.interfaces.rest;

import com.noctiva.noxway.shared.domain.RuleViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiErrors {
  @ExceptionHandler(RuleViolation.class)
  ResponseEntity<ProblemDetail> rule(RuleViolation e) {
    return problem(422, e.getMessage());
  }

  @ExceptionHandler(ResponseStatusException.class)
  ResponseEntity<ProblemDetail> status(ResponseStatusException e) {
    return problem(e.getStatusCode().value(), e.getReason());
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class
  })
  ResponseEntity<ProblemDetail> validation(Exception e) {
    return problem(400, "Invalid request fields or format");
  }

  @ExceptionHandler({
    DataIntegrityViolationException.class,
    ObjectOptimisticLockingFailureException.class
  })
  ResponseEntity<ProblemDetail> conflict(Exception e) {
    return problem(409, "Duplicate resource, concurrent update or referenced resource");
  }

  private ResponseEntity<ProblemDetail> problem(int status, String message) {
    return ResponseEntity.status(status)
        .body(
            ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(status), message == null ? "Request failed" : message));
  }
}
