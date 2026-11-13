package com.noctiva.noxway.commuting.interfaces.rest;

import com.noctiva.noxway.commuting.application.AlertDispatchService;
import com.noctiva.noxway.commuting.interfaces.rest.transform.CommutingRestAssembler;
import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alerts/deliveries")
@Tag(name = "Alert Deliveries", description = "External notification delivery queue and retry controls")
public class AlertDeliveryController {

  private final AlertDispatchService dispatchService;
  private final IamContextFacade iamFacade;
  private final ResponseEntityAssembler responseAssembler;

  public AlertDeliveryController(
      AlertDispatchService dispatchService,
      IamContextFacade iamFacade,
      ResponseEntityAssembler responseAssembler) {
    this.dispatchService = dispatchService;
    this.iamFacade = iamFacade;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping
  @Operation(summary = "List external alert delivery records")
  public ResponseEntity<?> queue(HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    if (iamFacade.isSecurityEnabled() && !actor.isStaff()) {
      return responseAssembler.toErrorResponse(
          new ApplicationError.ForbiddenError("Moderator role required"), request, locale);
    }
    return responseAssembler.toOk(
        dispatchService.queue(), CommutingRestAssembler::toAlertDeliveryResources, request, locale);
  }

  @PostMapping("/{id}/retry")
  @Operation(summary = "Retry a failed external alert delivery")
  public ResponseEntity<?> retry(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    if (iamFacade.isSecurityEnabled() && !actor.isStaff()) {
      return responseAssembler.toErrorResponse(
          new ApplicationError.ForbiddenError("Moderator role required"), request, locale);
    }
    return responseAssembler.toOk(
        dispatchService.retry(id), CommutingRestAssembler::toResource, request, locale);
  }

  @PostMapping("/dispatch")
  @Operation(summary = "Trigger immediate dispatch of pending external alerts")
  public ResponseEntity<Map<String, Integer>> dispatchNow() {
    return ResponseEntity.ok(Map.of("processed", dispatchService.dispatch()));
  }
}
