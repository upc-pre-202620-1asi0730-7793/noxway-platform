package com.noctiva.noxway;

import static org.junit.jupiter.api.Assertions.*;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.domain.RuleViolation;
import com.noctiva.noxway.shared.interfaces.rest.GlobalExceptionHandler;
import com.noctiva.noxway.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

  private ProblemDetailFromApplicationErrorAssembler assembler;
  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("messages", "ValidationMessages");
    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
    messageSource.setFallbackToSystemLocale(false);
    assembler = new ProblemDetailFromApplicationErrorAssembler(messageSource);
    handler = new GlobalExceptionHandler(messageSource);
  }

  @Test
  void convertsApplicationErrorsToRfc9457WithEnglishAndSpanishLocalization() {
    var notImplemented =
        new ApplicationError.NotImplementedError(
            "iam.not_implemented", "IAM authentication is structured and pending.");
    ProblemDetail enProblem =
        assembler.toProblemDetail(notImplemented, Locale.ENGLISH, "/api/v1/auth/sign-in");
    assertEquals(HttpStatus.NOT_IMPLEMENTED.value(), enProblem.getStatus());
    assertEquals(URI.create("https://api.noxway.pe/problems/not-implemented"), enProblem.getType());
    assertEquals(URI.create("/api/v1/auth/sign-in"), enProblem.getInstance());
    assertEquals("Feature not implemented yet", enProblem.getTitle());
    assertEquals("iam.not_implemented", enProblem.getProperties().get("errorKey"));
    assertNotNull(enProblem.getProperties().get("timestamp"));

    ProblemDetail esProblem =
        assembler.toProblemDetail(
            notImplemented, Locale.forLanguageTag("es"), "/api/v1/auth/sign-in");
    assertEquals(HttpStatus.NOT_IMPLEMENTED.value(), esProblem.getStatus());
    assertEquals("Funcionalidad aún no implementada", esProblem.getTitle());
    assertTrue(esProblem.getDetail().contains("pendiente de implementaci"));

    var limitError =
        new ApplicationError.BusinessRuleError("profiles.trust_link.limit_reached", "Limit", 1);
    ProblemDetail esLimit =
        assembler.toProblemDetail(limitError, Locale.forLanguageTag("es"), "/api/v1/trust-links");
    assertEquals(422, esLimit.getStatus());
    assertTrue(esLimit.getDetail().contains("m\u00e1ximo de 1"));
  }

  @Test
  void mapsAllApplicationErrorVariantsToExpectedHttpStatus() {
    assertEquals(
        HttpStatus.BAD_REQUEST,
        assembler.statusFor(new ApplicationError.ValidationError("Invalid")));
    assertEquals(
        HttpStatus.NOT_FOUND,
        assembler.statusFor(new ApplicationError.NotFoundError("Missing")));
    assertEquals(
        HttpStatus.CONFLICT,
        assembler.statusFor(new ApplicationError.ConflictError("Conflict")));
    assertEquals(
        HttpStatus.UNPROCESSABLE_ENTITY,
        assembler.statusFor(new ApplicationError.BusinessRuleError("Rule")));
    assertEquals(
        HttpStatus.FORBIDDEN,
        assembler.statusFor(new ApplicationError.ForbiddenError("Denied")));
    assertEquals(
        HttpStatus.NOT_IMPLEMENTED,
        assembler.statusFor(new ApplicationError.NotImplementedError("iam.not_implemented", "Pending")));
    assertEquals(
        HttpStatus.SERVICE_UNAVAILABLE,
        assembler.statusFor(new ApplicationError.ServiceUnavailableError("Down")));
  }

  @Test
  void globalExceptionHandlerProducesProblemDetailForDomainAndConflictExceptions() {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/test");

    var ruleResp =
        handler.handleRuleViolation(new RuleViolation("Invalid window"), request, Locale.ENGLISH);
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ruleResp.getStatusCode());
    assertEquals("Invalid window", ruleResp.getBody().getDetail());

    var conflictResp =
        handler.handleConflict(
            new DataIntegrityViolationException("duplicate key"),
            request,
            Locale.forLanguageTag("es"));
    assertEquals(HttpStatus.CONFLICT, conflictResp.getStatusCode());
    assertEquals("Conflicto de recurso", conflictResp.getBody().getTitle());
  }
}
