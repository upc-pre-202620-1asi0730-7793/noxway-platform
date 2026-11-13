package com.noctiva.noxway.shared.application.result;

/**
 * Sealed application-layer error hierarchy translated to RFC 9457 ProblemDetail at the REST boundary.
 */
public sealed interface ApplicationError {

  String messageKey();

  Object[] args();

  String defaultMessage();

  record ValidationError(String messageKey, Object[] args, String defaultMessage)
      implements ApplicationError {
    public ValidationError(String defaultMessage) {
      this("error.validation.detail", new Object[] {defaultMessage}, defaultMessage);
    }

    public ValidationError(String messageKey, String defaultMessage, Object... args) {
      this(messageKey, args, defaultMessage);
    }
  }

  record NotFoundError(String messageKey, Object[] args, String defaultMessage)
      implements ApplicationError {
    public NotFoundError(String defaultMessage) {
      this("error.not_found.detail", new Object[] {defaultMessage}, defaultMessage);
    }

    public NotFoundError(String messageKey, String defaultMessage, Object... args) {
      this(messageKey, args, defaultMessage);
    }
  }

  record ConflictError(String messageKey, Object[] args, String defaultMessage)
      implements ApplicationError {
    public ConflictError(String defaultMessage) {
      this("error.conflict.detail", new Object[] {defaultMessage}, defaultMessage);
    }

    public ConflictError(String messageKey, String defaultMessage, Object... args) {
      this(messageKey, args, defaultMessage);
    }
  }

  record BusinessRuleError(String messageKey, Object[] args, String defaultMessage)
      implements ApplicationError {
    public BusinessRuleError(String defaultMessage) {
      this("error.business_rule.detail", new Object[] {defaultMessage}, defaultMessage);
    }

    public BusinessRuleError(String messageKey, String defaultMessage, Object... args) {
      this(messageKey, args, defaultMessage);
    }
  }

  record ForbiddenError(String messageKey, Object[] args, String defaultMessage)
      implements ApplicationError {
    public ForbiddenError(String defaultMessage) {
      this("error.forbidden.detail", new Object[] {defaultMessage}, defaultMessage);
    }
  }

  record NotImplementedError(String messageKey, Object[] args, String defaultMessage)
      implements ApplicationError {
    public NotImplementedError(String messageKey, String defaultMessage) {
      this(messageKey, new Object[0], defaultMessage);
    }
  }

  record ServiceUnavailableError(String messageKey, Object[] args, String defaultMessage)
      implements ApplicationError {
    public ServiceUnavailableError(String defaultMessage) {
      this("error.service_unavailable.detail", new Object[] {defaultMessage}, defaultMessage);
    }
  }
}
