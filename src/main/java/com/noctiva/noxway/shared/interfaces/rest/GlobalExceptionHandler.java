package com.noctiva.noxway.shared.interfaces.rest;

import com.noctiva.noxway.shared.domain.RuleViolation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Centralized RFC 9457 ProblemDetail exception translator with localized messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final MessageSource messageSource;

  public GlobalExceptionHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(RuleViolation.class)
  public ResponseEntity<ProblemDetail> handleRuleViolation(
      RuleViolation ex, HttpServletRequest request, Locale locale) {
    return build(
        HttpStatus.UNPROCESSABLE_ENTITY,
        "business-rule",
        "error.business_rule.title",
        ex.getMessage(),
        request,
        locale,
        null);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ProblemDetail> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request, Locale locale) {
    return build(
        HttpStatus.BAD_REQUEST,
        "validation",
        "error.validation.title",
        ex.getMessage(),
        request,
        locale,
        null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request, Locale locale) {
    Map<String, String> fieldErrors = new LinkedHashMap<>();
    for (var err : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(err.getField(), err.getDefaultMessage());
    }
    String detail =
        messageSource.getMessage(
            "error.validation.detail",
            new Object[] {fieldErrors.toString()},
            "Validation failed: " + fieldErrors,
            locale);
    return build(
        HttpStatus.BAD_REQUEST,
        "validation",
        "error.validation.title",
        detail,
        request,
        locale,
        Map.of("errors", fieldErrors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request, Locale locale) {
    return build(
        HttpStatus.BAD_REQUEST,
        "validation",
        "error.validation.title",
        ex.getMessage(),
        request,
        locale,
        null);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request, Locale locale) {
    return build(
        HttpStatus.BAD_REQUEST,
        "validation",
        "error.validation.title",
        "Invalid parameter '" + ex.getName() + "'",
        request,
        locale,
        null);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleUnreadable(
      HttpMessageNotReadableException ex, HttpServletRequest request, Locale locale) {
    String detail =
        messageSource.getMessage(
            "error.malformed_json.detail", null, "Malformed JSON request body", locale);
    return build(
        HttpStatus.BAD_REQUEST,
        "malformed-json",
        "error.malformed_json.title",
        detail,
        request,
        locale,
        null);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ProblemDetail> handleNoSuchElement(
      NoSuchElementException ex, HttpServletRequest request, Locale locale) {
    return build(
        HttpStatus.NOT_FOUND,
        "not-found",
        "error.not_found.title",
        ex.getMessage(),
        request,
        locale,
        null);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetail> handleNoResource(
      NoResourceFoundException ex, HttpServletRequest request, Locale locale) {
    return build(
        HttpStatus.NOT_FOUND,
        "not-found",
        "error.not_found.title",
        ex.getMessage(),
        request,
        locale,
        null);
  }

  @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class})
  public ResponseEntity<ProblemDetail> handleConflict(
      RuntimeException ex, HttpServletRequest request, Locale locale) {
    String detail =
        messageSource.getMessage(
            "error.conflict.detail",
            new Object[] {"Concurrent modification or duplicate constraint"},
            "Resource conflict",
            locale);
    return build(
        HttpStatus.CONFLICT, "conflict", "error.conflict.title", detail, request, locale, null);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ProblemDetail> handleResponseStatus(
      ResponseStatusException ex, HttpServletRequest request, Locale locale) {
    HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
    if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
    return build(
        status,
        status.name().toLowerCase(Locale.ROOT).replace('_', '-'),
        "error.internal.title",
        ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(),
        request,
        locale,
        null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(
      Exception ex, HttpServletRequest request, Locale locale) {
    String detail =
        messageSource.getMessage("error.internal.detail", null, "Unexpected server error", locale);
    return build(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "internal-error",
        "error.internal.title",
        detail,
        request,
        locale,
        null);
  }

  private ResponseEntity<ProblemDetail> build(
      HttpStatus status,
      String typeSuffix,
      String titleKey,
      String detail,
      HttpServletRequest request,
      Locale locale,
      Map<String, Object> extra) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
    pd.setType(URI.create("https://api.noxway.pe/problems/" + typeSuffix));
    pd.setTitle(messageSource.getMessage(titleKey, null, status.getReasonPhrase(), locale));
    pd.setProperty("timestamp", Instant.now().toString());
    if (request != null) {
      pd.setInstance(URI.create(request.getRequestURI()));
    }
    if (extra != null) {
      extra.forEach(pd::setProperty);
    }
    return ResponseEntity.status(status).body(pd);
  }
}
