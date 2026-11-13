package com.noctiva.noxway.shared.interfaces.rest.transform;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import java.net.URI;
import java.time.Instant;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

/**
 * Converts {@link ApplicationError} cases into localized RFC 9457 {@link ProblemDetail} payloads.
 */
@Component
public class ProblemDetailFromApplicationErrorAssembler {

  private final MessageSource messageSource;

  public ProblemDetailFromApplicationErrorAssembler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public ProblemDetail toProblemDetail(ApplicationError error, Locale locale, String instancePath) {
    HttpStatus status = statusFor(error);
    String titleKey = titleKeyFor(error);
    String typeSuffix = typeSuffixFor(error);

    String title = messageSource.getMessage(titleKey, null, status.getReasonPhrase(), locale);
    String detail =
        messageSource.getMessage(error.messageKey(), error.args(), error.defaultMessage(), locale);

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setType(URI.create("https://api.noxway.pe/problems/" + typeSuffix));
    problem.setTitle(title);
    problem.setProperty("errorKey", error.messageKey());
    problem.setProperty("timestamp", Instant.now().toString());
    if (instancePath != null && !instancePath.isBlank()) {
      problem.setInstance(URI.create(instancePath));
    }
    return problem;
  }

  public HttpStatus statusFor(ApplicationError error) {
    return switch (error) {
      case ApplicationError.ValidationError ignored -> HttpStatus.BAD_REQUEST;
      case ApplicationError.NotFoundError ignored -> HttpStatus.NOT_FOUND;
      case ApplicationError.ConflictError ignored -> HttpStatus.CONFLICT;
      case ApplicationError.BusinessRuleError ignored -> HttpStatus.UNPROCESSABLE_ENTITY;
      case ApplicationError.ForbiddenError ignored -> HttpStatus.FORBIDDEN;
      case ApplicationError.NotImplementedError ignored -> HttpStatus.NOT_IMPLEMENTED;
      case ApplicationError.ServiceUnavailableError ignored -> HttpStatus.SERVICE_UNAVAILABLE;
    };
  }

  private String titleKeyFor(ApplicationError error) {
    return switch (error) {
      case ApplicationError.ValidationError ignored -> "error.validation.title";
      case ApplicationError.NotFoundError ignored -> "error.not_found.title";
      case ApplicationError.ConflictError ignored -> "error.conflict.title";
      case ApplicationError.BusinessRuleError ignored -> "error.business_rule.title";
      case ApplicationError.ForbiddenError ignored -> "error.forbidden.title";
      case ApplicationError.NotImplementedError ignored -> "error.not_implemented.title";
      case ApplicationError.ServiceUnavailableError ignored -> "error.service_unavailable.title";
    };
  }

  private String typeSuffixFor(ApplicationError error) {
    return switch (error) {
      case ApplicationError.ValidationError ignored -> "validation";
      case ApplicationError.NotFoundError ignored -> "not-found";
      case ApplicationError.ConflictError ignored -> "conflict";
      case ApplicationError.BusinessRuleError ignored -> "business-rule";
      case ApplicationError.ForbiddenError ignored -> "forbidden";
      case ApplicationError.NotImplementedError ignored -> "not-implemented";
      case ApplicationError.ServiceUnavailableError ignored -> "service-unavailable";
    };
  }
}
