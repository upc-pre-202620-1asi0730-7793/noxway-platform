package com.noctiva.noxway;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noctiva.noxway.shared.infrastructure.configuration.BootstrapData;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NoxwayPlatformIntegrationTest {

  private static final String WORKER_PRIMARY = BootstrapData.WORKER_PRIMARY_ID.toString();
  private static final String WORKER_SECONDARY = BootstrapData.WORKER_SECONDARY_ID.toString();
  private static final String CONTACT_PRIMARY = BootstrapData.CONTACT_PRIMARY_ID.toString();
  private static final String CONTACT_SECONDARY = BootstrapData.CONTACT_SECONDARY_ID.toString();
  private static final String ADMIN = BootstrapData.ADMIN_ID.toString();

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  private JsonNode call(String method, String path, String actorId, Object body, int expected)
      throws Exception {
    return call(method, path, actorId, null, body, expected);
  }

  private JsonNode call(
      String method, String path, String actorId, String acceptLang, Object body, int expected)
      throws Exception {
    var req =
        MockMvcRequestBuilders.request(HttpMethod.valueOf(method), path)
            .contentType(MediaType.APPLICATION_JSON);
    if (actorId != null) {
      req.header("X-Actor-Id", actorId);
    }
    if (acceptLang != null) {
      req.header("Accept-Language", acceptLang);
    }
    if (body != null) {
      req.content(json.writeValueAsString(body));
    }
    var result = mvc.perform(req).andReturn();
    String text = result.getResponse().getContentAsString();
    assertEquals(
        expected,
        result.getResponse().getStatus(),
        method + " " + path + " (actor=" + actorId + "): " + text);
    return text.isBlank() ? json.createObjectNode() : json.readTree(text);
  }

  private String createAccount(String role) throws Exception {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    var body =
        role.equals("WORKER")
            ? Map.of(
                "email", "w-" + suffix + "@noxway.test",
                "fullName", "Worker " + suffix,
                "phone", "+51988112233",
                "role", "WORKER",
                "shiftType", "NIGHT",
                "shiftStart", "22:00",
                "shiftEnd", "06:00",
                "timeZone", "America/Lima")
            : Map.of(
                "email", "c-" + suffix + "@noxway.test",
                "fullName", "Contact " + suffix,
                "phone", "+51988445566",
                "role", "TRUSTED_CONTACT",
                "timeZone", "America/Lima");
    return call("POST", "/api/v1/accounts", null, body, 201).get("id").asText();
  }

  @Test
  void structuredIamDeferredStatusAndLocalizedProblemDetails() throws Exception {
    JsonNode status = call("GET", "/api/v1/auth/status", null, null, 200);
    assertFalse(status.get("securityEnabled").asBoolean());
    assertEquals("STRUCTURED_PENDING_IMPLEMENTATION", status.get("mode").asText());

    JsonNode signInEn =
        call(
            "POST",
            "/api/v1/auth/sign-in",
            null,
            "en",
            Map.of("email", "jorge.huaman@noxway.pe", "password", "secret123456"),
            501);
    assertEquals("https://api.noxway.pe/problems/not-implemented", signInEn.get("type").asText());
    assertEquals("Feature not implemented yet", signInEn.get("title").asText());

    JsonNode signInEs =
        call(
            "POST",
            "/api/v1/auth/sign-in",
            null,
            "es",
            Map.of("email", "jorge.huaman@noxway.pe", "password", "secret123456"),
            501);
    assertEquals("Funcionalidad a\u00fan no implementada", signInEs.get("title").asText());
    assertTrue(signInEs.get("detail").asText().contains("app.security.enabled=false"));
  }

  @Test
  void profilesTrustLinksAndPlanContactAllowanceLifecycle() throws Exception {
    JsonNode defaultMe = call("GET", "/api/v1/accounts/me", null, null, 200);
    assertEquals(WORKER_PRIMARY, defaultMe.get("id").asText());
    assertEquals("jorge.huaman@noxway.pe", defaultMe.get("email").asText());

    JsonNode updated =
        call(
            "PUT",
            "/api/v1/accounts/me",
            WORKER_PRIMARY,
            Map.of(
                "fullName", "Jorge Luis Huam\u00e1n (Verificado)",
                "phone", "+51987654321",
                "shiftType", "NIGHT",
                "shiftStart", "22:00",
                "shiftEnd", "06:00",
                "timeZone", "America/Lima"),
            200);
    assertEquals("Jorge Luis Huam\u00e1n (Verificado)", updated.get("fullName").asText());

    call(
        "PUT",
        "/api/v1/accounts/me/notification-preferences",
        CONTACT_PRIMARY,
        Map.of(
            "channel", "WHATSAPP",
            "enabled", true,
            "priorityStart", "20:00",
            "priorityEnd", "08:00",
            "timeZone", "America/Lima"),
        200);

    // Essential plan allows 1 contact; WORKER_PRIMARY already has CONTACT_PRIMARY active
    call(
        "POST",
        "/api/v1/trust-links",
        WORKER_PRIMARY,
        Map.of("contactEmail", "miguel.quispe@noxway.pe"),
        422);

    // Fresh worker + contact can invite -> reject -> re-invite -> accept -> revoke
    String workerId = createAccount("WORKER");
    String contactEmail = "tc-" + UUID.randomUUID().toString().substring(0, 6) + "@noxway.test";
    String contactId =
        call(
                "POST",
                "/api/v1/accounts",
                null,
                Map.of(
                    "email", contactEmail,
                    "fullName", "Nuevo Contacto",
                    "phone", "+51988000111",
                    "role", "TRUSTED_CONTACT",
                    "timeZone", "America/Lima"),
                201)
            .get("id")
            .asText();

    JsonNode invited =
        call("POST", "/api/v1/trust-links", workerId, Map.of("contactEmail", contactEmail), 201);
    String linkId = invited.get("id").asText();
    assertEquals("PENDING", invited.get("status").asText());

    JsonNode rejected = call("POST", "/api/v1/trust-links/" + linkId + "/reject", contactId, null, 200);
    assertEquals("REJECTED", rejected.get("status").asText());

    JsonNode reInvited =
        call("POST", "/api/v1/trust-links", workerId, Map.of("contactEmail", contactEmail), 201);
    String newLinkId = reInvited.get("id").asText();
    JsonNode accepted = call("POST", "/api/v1/trust-links/" + newLinkId + "/accept", contactId, null, 200);
    assertEquals("ACTIVE", accepted.get("status").asText());

    call("DELETE", "/api/v1/trust-links/" + newLinkId, workerId, null, 204);
    JsonNode reInvitedAfterRevoke =
        call("POST", "/api/v1/trust-links", workerId, Map.of("contactEmail", contactEmail), 201);
    assertEquals("PENDING", reInvitedAfterRevoke.get("status").asText());
  }

  @Test
  void wellnessSessionsOverlapGoalAndBreakdownSummary() throws Exception {
    String workerId = createAccount("WORKER");
    Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
    Instant start = now.minus(6, ChronoUnit.HOURS);

    JsonNode logged =
        call(
            "POST",
            "/api/v1/wellness/rest-sessions",
            workerId,
            Map.of(
                "startTime", start.toString(),
                "endTime", now.toString(),
                "quality", 4,
                "notes", "Descanso post turno"),
            201);
    String sessionId = logged.get("id").asText();
    assertEquals(360, logged.get("durationMinutes").asInt());

    // Overlapping session rejected
    call(
        "POST",
        "/api/v1/wellness/rest-sessions",
        workerId,
        Map.of(
            "startTime", start.plus(1, ChronoUnit.HOURS).toString(),
            "endTime", now.minus(1, ChronoUnit.HOURS).toString(),
            "quality", 3,
            "notes", "Overlap"),
        422);

    // Other actor cannot delete someone else's session
    call("DELETE", "/api/v1/wellness/rest-sessions/" + sessionId, WORKER_SECONDARY, null, 403);

    call("PUT", "/api/v1/wellness/goal", workerId, Map.of("targetMinutes", 480), 200);
    JsonNode summary = call("GET", "/api/v1/wellness/summary?timeZone=America/Lima", workerId, null, 200);
    assertEquals(480, summary.get("targetDailyMinutes").asInt());
    assertTrue(summary.get("totalMinutes").asLong() >= 360);
    assertEquals(7, summary.get("dailyBreakdown").size());

    call("DELETE", "/api/v1/wellness/rest-sessions/" + sessionId, workerId, null, 204);
  }

  @Test
  void subscriptionsOfflinePaymentsReferralsBenefitsAndFamilyCrew() throws Exception {
    JsonNode plans = call("GET", "/api/v1/plans", null, null, 200);
    String proPlanId = null;
    for (JsonNode p : plans) {
      if ("SENTINEL_PRO".equals(p.get("code").asText())) {
        proPlanId = p.get("id").asText();
      }
    }
    assertNotNull(proPlanId);

    String workerA = createAccount("WORKER");
    String workerB = createAccount("WORKER");

    String refCode = call("GET", "/api/v1/referral-code", workerB, null, 200).get("code").asText();
    call("POST", "/api/v1/referrals", workerA, Map.of("code", refCode), 201);

    JsonNode subA = call("POST", "/api/v1/subscriptions", workerA, Map.of("planId", proPlanId), 201);
    assertEquals("PENDING_PAYMENT", subA.get("status").asText());

    JsonNode paymentsA = call("GET", "/api/v1/payments", workerA, null, 200).get("items");
    assertFalse(paymentsA.isEmpty());
    String paymentId = paymentsA.get(0).get("id").asText();

    String uniqueReceipt = "YAPE-" + UUID.randomUUID().toString().substring(0, 8);
    JsonNode confirmed =
        call(
            "POST",
            "/api/v1/payments/" + paymentId + "/confirm-offline",
            ADMIN,
            Map.of("reference", uniqueReceipt),
            200);
    assertEquals("CONFIRMED", confirmed.get("status").asText());

    // Duplicate receipt reference rejected on second worker payment
    JsonNode subB = call("POST", "/api/v1/subscriptions", workerB, Map.of("planId", proPlanId), 201);
    assertEquals("PENDING_PAYMENT", subB.get("status").asText());
    String paymentBId = call("GET", "/api/v1/payments", workerB, null, 200).get("items").get(0).get("id").asText();
    call(
        "POST",
        "/api/v1/payments/" + paymentBId + "/confirm-offline",
        ADMIN,
        Map.of("reference", uniqueReceipt),
        409);

    // Referral reward triggered on workerA's first confirmed payment -> workerB can redeem it
    JsonNode rewardsB = call("GET", "/api/v1/referral-rewards", workerB, null, 200);
    assertEquals(1, rewardsB.size());
    String rewardId = rewardsB.get(0).get("id").asText();
    JsonNode redeemed = call("POST", "/api/v1/referral-rewards/" + rewardId + "/redeem", workerB, null, 200);
    assertEquals("REDEEMED", redeemed.get("status").asText());

    // Collective benefit claim requires SENTINEL_PRO -> workerA succeeds, fresh essential worker fails
    JsonNode benefits = call("GET", "/api/v1/benefits", workerA, null, 200);
    String proBenefitId = null;
    for (JsonNode b : benefits) {
      if ("SENTINEL_PRO".equals(b.get("planCode").asText())) {
        proBenefitId = b.get("id").asText();
        break;
      }
    }
    assertNotNull(proBenefitId);
    JsonNode claim = call("POST", "/api/v1/benefits/" + proBenefitId + "/claims", workerA, null, 201);
    assertTrue(claim.get("code").asText().startsWith("NOX-"));

    String essentialWorker = createAccount("WORKER");
    call("POST", "/api/v1/benefits/" + proBenefitId + "/claims", essentialWorker, null, 422);

    // Ownership guard on subscription cancel
    call("POST", "/api/v1/subscriptions/" + subA.get("id").asText() + "/cancel", workerB, null, 403);

    // WORKER_PRIMARY accepts seeded FAMILY_CREW invitation from WORKER_SECONDARY
    JsonNode invitations = call("GET", "/api/v1/subscription-invitations", WORKER_PRIMARY, null, 200);
    assertFalse(invitations.isEmpty());
    String invitationId = invitations.get(0).get("id").asText();
    JsonNode acceptedInvite =
        call("POST", "/api/v1/subscription-invitations/" + invitationId + "/accept", WORKER_PRIMARY, null, 200);
    assertEquals("ACCEPTED", acceptedInvite.get("status").asText());
  }

  @Test
  void communityModerationAndCommutingCompanionLiveFlow() throws Exception {
    // 1. Community submission -> Moderation -> Voting
    JsonNode submittedService =
        call(
            "POST",
            "/api/v1/community/services",
            WORKER_PRIMARY,
            Map.of(
                "name", "Grifo 24h Surquillo Test",
                "type", "GAS_STATION",
                "latitude", -12.111,
                "longitude", -77.019,
                "timeZone", "America/Lima",
                "openingHours",
                java.util.List.of(
                    Map.of(
                        "dayOfWeek", 1,
                        "opensAt", "00:00",
                        "closesAt", "00:00",
                        "closesNextDay", false,
                        "is24Hours", true))),
            201);
    String serviceId = submittedService.get("id").asText();
    assertEquals("PENDING", submittedService.get("status").asText());

    // Cannot vote on PENDING service
    call(
        "PUT",
        "/api/v1/community/services/" + serviceId + "/vote",
        WORKER_SECONDARY,
        Map.of("direction", 1),
        422);

    // Moderator approves service in queue
    JsonNode pendingReports = call("GET", "/api/v1/moderation/reports?status=PENDING", ADMIN, null, 200).get("items");
    String serviceReportId = null;
    for (JsonNode r : pendingReports) {
      if (serviceId.equals(r.get("targetId").asText())) {
        serviceReportId = r.get("id").asText();
        break;
      }
    }
    assertNotNull(serviceReportId);
    call(
        "POST",
        "/api/v1/moderation/reports/" + serviceReportId + "/decision",
        ADMIN,
        Map.of("decision", "APPROVE", "notes", "Verified by integration test"),
        200);
    JsonNode actions =
        call("GET", "/api/v1/moderation/reports/" + serviceReportId + "/actions", ADMIN, null, 200);
    assertEquals(1, actions.size());

    JsonNode voted =
        call(
            "PUT",
            "/api/v1/community/services/" + serviceId + "/vote",
            WORKER_SECONDARY,
            Map.of("direction", 1),
            200);
    assertEquals(1, voted.get("direction").asInt());
    JsonNode refreshedService =
        call("GET", "/api/v1/community/services/" + serviceId, WORKER_SECONDARY, null, 200);
    assertEquals(1, refreshedService.get("trustScore").asInt());

    // Post ownership test
    JsonNode post =
        call(
            "POST",
            "/api/v1/community/posts",
            WORKER_PRIMARY,
            Map.of("title", "Tramo iluminado", "content", "Av. Arequipa con buena luz", "district", "Lince"),
            201);
    String postId = post.get("id").asText();
    call("DELETE", "/api/v1/community/posts/" + postId, WORKER_SECONDARY, null, 403);
    call("DELETE", "/api/v1/community/posts/" + postId, ADMIN, null, 204);

    // 2. Commute + telemetry + delay + SOS incident -> Companion live & alerts -> arrive -> rate route
    Instant start = Instant.now().minusSeconds(60);
    Instant eta = start.plus(35, ChronoUnit.MINUTES);
    JsonNode commute =
        call(
            "POST",
            "/api/v1/commutes",
            WORKER_PRIMARY,
            Map.of(
                "origin", "Hospital Rebagliati",
                "destination", "San Miguel",
                "transport", "BUS",
                "startTime", start.toString(),
                "estimatedArrivalTime", eta.toString(),
                "toleranceMinutes", 10),
            201);
    String commuteId = commute.get("id").asText();

    // Second simultaneous active commute rejected
    call(
        "POST",
        "/api/v1/commutes",
        WORKER_PRIMARY,
        Map.of(
            "origin", "B",
            "destination", "C",
            "transport", "TAXI",
            "startTime", start.toString(),
            "estimatedArrivalTime", eta.toString(),
            "toleranceMinutes", 5),
        409);

    call(
        "POST",
        "/api/v1/commutes/" + commuteId + "/telemetry",
        WORKER_PRIMARY,
        Map.of(
            "latitude", -12.081,
            "longitude", -77.042,
            "speed", 24.5,
            "recordedAt", Instant.now().toString()),
        201);

    JsonNode delayed =
        call("POST", "/api/v1/commutes/" + commuteId + "/delay", WORKER_PRIMARY, Map.of("minutes", 15), 200);
    assertEquals("DELAYED", delayed.get("status").asText());

    JsonNode incident =
        call(
            "POST",
            "/api/v1/commutes/" + commuteId + "/incidents",
            WORKER_PRIMARY,
            Map.of("type", "SOS", "description", "Paradero sin luz"),
            201);
    String incidentId = incident.get("id").asText();

    // Companion Rosa checks live feed & alerts
    JsonNode live = call("GET", "/api/v1/companion/live", CONTACT_PRIMARY, null, 200);
    assertFalse(live.isEmpty());
    boolean sawJorgeLive = false;
    for (JsonNode entry : live) {
      if (WORKER_PRIMARY.equals(entry.get("workerId").asText())) {
        sawJorgeLive = true;
        assertEquals(commuteId, entry.get("activeCommute").get("id").asText());
        assertNotNull(entry.get("latestPing"));
        assertFalse(entry.get("openIncidents").isEmpty());
      }
    }
    assertTrue(sawJorgeLive);

    JsonNode alerts = call("GET", "/api/v1/companion/alerts", CONTACT_PRIMARY, null, 200).get("items");
    assertTrue(alerts.size() >= 1);
    assertTrue(
        java.util.stream.StreamSupport.stream(alerts.spliterator(), false)
            .anyMatch(
                a ->
                    "IN_APP".equals(a.get("channel").asText())
                        && "DELIVERED".equals(a.get("status").asText())));

    // Run alert dispatcher
    call("POST", "/api/v1/alerts/deliveries/dispatch", CONTACT_PRIMARY, null, 200);

    // Cannot rate route before arriving
    call(
        "POST",
        "/api/v1/community/route-ratings",
        WORKER_PRIMARY,
        Map.of("commuteId", commuteId, "score", 5, "comments", "Too early"),
        422);

    call("POST", "/api/v1/incidents/" + incidentId + "/resolve", WORKER_PRIMARY, null, 200);
    JsonNode arrived = call("POST", "/api/v1/commutes/" + commuteId + "/arrive", WORKER_PRIMARY, null, 200);
    assertEquals("ARRIVED", arrived.get("status").asText());

    JsonNode rating =
        call(
            "POST",
            "/api/v1/community/route-ratings",
            WORKER_PRIMARY,
            Map.of("commuteId", commuteId, "score", 5, "comments", "Llegada segura"),
            201);
    assertEquals(5, rating.get("score").asInt());

    // Secondary contact was not linked to WORKER_PRIMARY
    assertEquals(0, call("GET", "/api/v1/companion/live", CONTACT_SECONDARY, null, 200).size());
  }
}
