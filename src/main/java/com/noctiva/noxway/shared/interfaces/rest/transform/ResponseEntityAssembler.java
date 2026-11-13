package com.noctiva.noxway.shared.interfaces.rest.transform;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Locale;
import java.util.function.Function;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Translates {@link Result} outcomes from application services into HTTP {@link ResponseEntity} responses.
 */
@Component
public class ResponseEntityAssembler {

  private final ProblemDetailFromApplicationErrorAssembler problemAssembler;

  public ResponseEntityAssembler(ProblemDetailFromApplicationErrorAssembler problemAssembler) {
    this.problemAssembler = problemAssembler;
  }

  public <T, R> ResponseEntity<?> toOk(
      Result<T, ApplicationError> result,
      Function<? super T, ? extends R> mapper,
      HttpServletRequest request,
      Locale locale) {
    return result.fold(
        value -> ResponseEntity.ok(mapper.apply(value)),
        error -> toErrorResponse(error, request, locale));
  }

  public <T, R> ResponseEntity<?> toCreated(
      Result<T, ApplicationError> result,
      Function<? super T, ? extends R> mapper,
      Function<? super T, URI> locationProvider,
      HttpServletRequest request,
      Locale locale) {
    return result.fold(
        value -> ResponseEntity.created(locationProvider.apply(value)).body(mapper.apply(value)),
        error -> toErrorResponse(error, request, locale));
  }

  public ResponseEntity<?> toNoContent(
      Result<Void, ApplicationError> result, HttpServletRequest request, Locale locale) {
    return result.fold(
        ignored -> ResponseEntity.noContent().build(),
        error -> toErrorResponse(error, request, locale));
  }

  public ResponseEntity<?> toErrorResponse(
      ApplicationError error, HttpServletRequest request, Locale locale) {
    String path = request != null ? request.getRequestURI() : null;
    var problem = problemAssembler.toProblemDetail(error, locale, path);
    return ResponseEntity.status(problemAssembler.statusFor(error)).body(problem);
  }
}
