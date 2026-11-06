package com.noctiva.noxway;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  record User(String id, String email, String token) {}

  JsonNode call(String method, String path, String token, Object body, int expected)
      throws Exception {
    var request =
        MockMvcRequestBuilders.request(org.springframework.http.HttpMethod.valueOf(method), path)
            .contentType(MediaType.APPLICATION_JSON);
    if (token != null) request.header("Authorization", "Bearer " + token);
    if (body != null) request.content(json.writeValueAsString(body));
    var result = mvc.perform(request).andReturn();
    assertEquals(
        expected,
        result.getResponse().getStatus(),
        method + " " + path + ": " + result.getResponse().getContentAsString());
    String text = result.getResponse().getContentAsString();
    return text.isBlank() ? json.createObjectNode() : json.readTree(text);
  }

  User user(String role) throws Exception {
    String email = UUID.randomUUID() + "@test.local";
    var fields = new LinkedHashMap<String, Object>();
    fields.put("email", email);
    fields.put("password", "TestingPassword!2026");
    fields.put("fullName", "Test User");
    fields.put("phone", "987654321");
    fields.put("role", role);
    fields.put("timeZone", "America/Lima");
    if (role.equals("WORKER")) {
      fields.put("shiftType", "NIGHT");
      fields.put("shiftStart", "22:00");
      fields.put("shiftEnd", "06:00");
    }
    var created = call("POST", "/api/v1/auth/register", null, fields, 201);
    assertFalse(created.has("passwordHash"));
    var login =
        call(
            "POST",
            "/api/v1/auth/login",
            null,
            Map.of("email", email, "password", "TestingPassword!2026"),
            200);
    return new User(created.get("id").asText(), email, login.get("accessToken").asText());
  }

  String admin() throws Exception {
    return call(
            "POST",
            "/api/v1/auth/login",
            null,
            Map.of("email", "admin@noxway.test", "password", "TestingAdmin!2026"),
            200)
        .get("accessToken")
        .asText();
  }

  String commute(User w) throws Exception {
    return call(
            "POST",
            "/api/v1/commutes",
            w.token(),
            Map.of(
                "origin",
                "Workplace",
                "destination",
                "Home",
                "transport",
                "BUS",
                "startTime",
                Instant.now().minusSeconds(10).toString(),
                "estimatedArrivalTime",
                Instant.now().plusSeconds(1800).toString(),
                "toleranceMinutes",
                10),
            201)
        .get("id")
        .asText();
  }

  @Test
  void consentTripIncidentPrivacyAndRating() throws Exception {
    var w = user("WORKER");
    var c = user("TRUSTED_CONTACT");
    var stranger = user("TRUSTED_CONTACT");
    String trip = commute(w);
    call("GET", "/api/v1/commutes/" + trip, stranger.token(), null, 403);
    var link =
        call("POST", "/api/v1/trust-links", w.token(), Map.of("contactEmail", c.email()), 201);
    String id = link.get("id").asText();
    call("GET", "/api/v1/commutes/" + trip, c.token(), null, 403);
    call("POST", "/api/v1/trust-links/" + id + "/accept", w.token(), null, 403);
    call("POST", "/api/v1/trust-links/" + id + "/accept", c.token(), null, 200);
    call("GET", "/api/v1/commutes/" + trip, c.token(), null, 200);
    call(
        "POST",
        "/api/v1/commutes/" + trip + "/telemetry",
        w.token(),
        Map.of(
            "latitude",
            -12.04,
            "longitude",
            -77.03,
            "speed",
            20,
            "recordedAt",
            Instant.now().toString()),
        201);
    call("GET", "/api/v1/commutes/" + trip + "/telemetry", c.token(), null, 200);
    var incident =
        call(
            "POST",
            "/api/v1/commutes/" + trip + "/incidents",
            w.token(),
            Map.of("type", "SOS", "description", "Request assistance"),
            201);
    assertTrue(call("GET", "/api/v1/alerts", c.token(), null, 200).get("total").asInt() > 0);
    call(
        "POST",
        "/api/v1/incidents/" + incident.get("id").asText() + "/resolve",
        w.token(),
        null,
        200);
    call("POST", "/api/v1/commutes/" + trip + "/arrival", w.token(), null, 200);
    call("POST", "/api/v1/commutes/" + trip + "/arrival", w.token(), null, 422);
    call("GET", "/api/v1/commutes/" + trip + "/telemetry", c.token(), null, 422);
    call(
        "POST",
        "/api/v1/community/route-ratings",
        w.token(),
        Map.of("commuteId", trip, "score", 5, "comments", "Good route"),
        201);
    call(
        "POST",
        "/api/v1/community/route-ratings",
        w.token(),
        Map.of("commuteId", trip, "score", 4, "comments", "Duplicate"),
        422);
    call("DELETE", "/api/v1/trust-links/" + id, w.token(), null, 200);
    call("GET", "/api/v1/commutes/" + trip, c.token(), null, 403);
  }

  @Test
  void singleActiveJourneyAndProfileValidation() throws Exception {
    var w = user("WORKER");
    commute(w);
    call(
        "POST",
        "/api/v1/commutes",
        w.token(),
        Map.of(
            "origin",
            "Work",
            "destination",
            "Home",
            "transport",
            "BUS",
            "startTime",
            Instant.now().minusSeconds(10).toString(),
            "estimatedArrivalTime",
            Instant.now().plusSeconds(900).toString(),
            "toleranceMinutes",
            5),
        422);
    var profile =
        call(
            "PUT",
            "/api/v1/accounts/me",
            w.token(),
            Map.of(
                "fullName",
                "Day Worker",
                "phone",
                "912345678",
                "shiftType",
                "DAY",
                "shiftStart",
                "08:00",
                "shiftEnd",
                "17:00",
                "timeZone",
                "America/Lima"),
            200);
    assertEquals("DAY", profile.get("shiftType").asText());
    call(
        "PUT",
        "/api/v1/accounts/me",
        w.token(),
        Map.of(
            "fullName",
            "Day Worker",
            "phone",
            "912345678",
            "shiftType",
            "INVALID",
            "shiftStart",
            "08:00",
            "shiftEnd",
            "17:00",
            "timeZone",
            "America/Lima"),
        422);
  }

  @Test
  void openingHoursModerationAndOvernightQuery() throws Exception {
    var w = user("WORKER");
    String admin = admin();
    var created =
        call(
            "POST",
            "/api/v1/community/services",
            w.token(),
            Map.of(
                "name",
                "Test Pharmacy",
                "type",
                "PHARMACY",
                "latitude",
                -12.04,
                "longitude",
                -77.03,
                "timeZone",
                "America/Lima",
                "openingHours",
                List.of(
                    Map.of(
                        "dayOfWeek",
                        1,
                        "opensAt",
                        "22:00",
                        "closesAt",
                        "06:00",
                        "closesNextDay",
                        true,
                        "is24Hours",
                        false))),
            201);
    String id = created.get("service").get("id").asText();
    var reports = call("GET", "/api/v1/moderation/reports?size=100", admin, null, 200).get("items");
    String reportId = "";
    for (var report : reports)
      if (report.get("targetId").asText().equals(id)) reportId = report.get("id").asText();
    assertFalse(reportId.isBlank());
    call(
        "POST",
        "/api/v1/moderation/reports/" + reportId + "/decision",
        w.token(),
        Map.of("decision", "APPROVE", "notes", "Verified"),
        403);
    call(
        "POST",
        "/api/v1/moderation/reports/" + reportId + "/decision",
        admin,
        Map.of("decision", "APPROVE", "notes", "Hours checked"),
        200);
    var overnight =
        call(
            "GET",
            "/api/v1/community/services/" + id + "?at=2026-11-03T06:00:00Z",
            w.token(),
            null,
            200);
    assertTrue(overnight.get("openAtReference").asBoolean());
    var closed =
        call(
            "GET",
            "/api/v1/community/services/" + id + "?at=2026-11-03T11:00:00Z",
            w.token(),
            null,
            200);
    assertFalse(closed.get("openAtReference").asBoolean());
    call(
        "PUT",
        "/api/v1/community/services/" + id + "/vote",
        w.token(),
        Map.of("direction", 1),
        200);
    call(
        "PUT",
        "/api/v1/community/services/" + id + "/vote",
        w.token(),
        Map.of("direction", -1),
        200);
    assertEquals(
        -1,
        call("GET", "/api/v1/community/services/" + id, w.token(), null, 200)
            .get("trustScore")
            .asInt());
  }

  @Test
  void restAcrossMidnightPersistsAndRejectsOverlap() throws Exception {
    var w = user("WORKER");
    var date = LocalDate.now(ZoneOffset.UTC).minusDays(3);
    String start = date + "T22:30:00Z", end = date.plusDays(1) + "T05:15:00Z";
    var body = Map.of("startTime", start, "endTime", end, "quality", 4, "notes", "Quiet room");
    call("POST", "/api/v1/wellness/rest-sessions", w.token(), body, 201);
    call("POST", "/api/v1/wellness/rest-sessions", w.token(), body, 422);
    assertEquals(
        1,
        call("GET", "/api/v1/wellness/rest-sessions", w.token(), null, 200).get("total").asInt());
    assertEquals(
        405,
        call("GET", "/api/v1/wellness/summary?timeZone=UTC", w.token(), null, 200)
            .get("totalMinutes")
            .asInt());
  }

  @Test
  void offlinePaymentsAndFamilyConsent() throws Exception {
    var owner = user("WORKER");
    var member = user("WORKER");
    String admin = admin();
    String planId = "";
    for (var p : call("GET", "/api/v1/plans", owner.token(), null, 200))
      if (p.get("code").asText().equals("FAMILY_CREW")) planId = p.get("id").asText();
    String id =
        call("POST", "/api/v1/subscriptions", owner.token(), Map.of("planId", planId), 201)
            .get("id")
            .asText();
    String payment =
        call("POST", "/api/v1/subscriptions/" + id + "/payments", owner.token(), null, 201)
            .get("id")
            .asText();
    call(
        "POST",
        "/api/v1/payments/" + payment + "/confirm-offline",
        owner.token(),
        Map.of("reference", "TEST-" + UUID.randomUUID()),
        422);
    call(
        "POST",
        "/api/v1/payments/" + payment + "/confirm-offline",
        admin,
        Map.of("reference", "TEST-" + UUID.randomUUID()),
        200);
    var doc = call("GET", "/v3/api-docs", null, null, 200);
    String schemaRef =
        doc.at(
                "/paths/~1api~1v1~1subscriptions~1{id}~1members/post/requestBody/content/application~1json/schema/$ref")
            .asText();
    String documentedEmailField = doc.at(schemaRef.substring(1)).path("required").get(0).asText();
    assertEquals("workerEmail", documentedEmailField);
    String invitation =
        call(
                "POST",
                "/api/v1/subscriptions/" + id + "/members",
                owner.token(),
                Map.of(documentedEmailField, member.email()),
                201)
            .get("id")
            .asText();
    call(
        "POST",
        "/api/v1/subscription-invitations/" + invitation + "/accept",
        member.token(),
        null,
        200);
    call("POST", "/api/v1/subscriptions/" + id + "/cancel", owner.token(), null, 200);
    call(
        "POST",
        "/api/v1/subscription-invitations/" + invitation + "/accept",
        member.token(),
        null,
        422);
  }

  @Test
  void sessionRevocationAndNoPublicRoleEscalation() throws Exception {
    var w = user("WORKER");
    call("GET", "/api/v1/accounts/me", null, null, 401);
    call(
        "POST",
        "/api/v1/auth/register",
        null,
        Map.of(
            "email",
            "escalation@test.local",
            "password",
            "TestingPassword!2026",
            "fullName",
            "Bad Actor",
            "phone",
            "123",
            "role",
            "ADMIN"),
        400);
    call(
        "PUT",
        "/api/v1/accounts/me/password",
        w.token(),
        Map.of("currentPassword", "TestingPassword!2026", "newPassword", "NewTestingPassword!2026"),
        204);
    call("GET", "/api/v1/accounts/me", w.token(), null, 401);
    call("POST", "/api/v1/auth/password-reset", null, Map.of("email", w.email()), 503);
  }

  @Test
  void invitationSchemasMatchTheirRequestBodies() throws Exception {
    var doc = call("GET", "/v3/api-docs", null, null, 200);
    String familyRef =
        doc.at(
                "/paths/~1api~1v1~1subscriptions~1{id}~1members/post/requestBody/content/application~1json/schema/$ref")
            .asText();
    String trustRef =
        doc.at(
                "/paths/~1api~1v1~1trust-links/post/requestBody/content/application~1json/schema/$ref")
            .asText();
    assertFalse(familyRef.isBlank());
    assertFalse(trustRef.isBlank());
    assertNotEquals(familyRef, trustRef, "Different invitation contracts must not share a schema");
    var family = doc.at(familyRef.substring(1));
    var trust = doc.at(trustRef.substring(1));
    assertTrue(family.path("properties").has("workerEmail"));
    assertFalse(family.path("properties").has("contactEmail"));
    assertTrue(trust.path("properties").has("contactEmail"));
    assertFalse(trust.path("properties").has("workerEmail"));
    assertTrue(family.path("required").toString().contains("workerEmail"));
    assertTrue(trust.path("required").toString().contains("contactEmail"));
  }

  @Test
  void apiDocumentationLoads() throws Exception {
    var doc = call("GET", "/v3/api-docs", null, null, 200);
    assertTrue(doc.get("paths").size() > 40);
  }

  @Test
  void referralsGenerateRedeemableCreditOnlyAfterFirstConfirmedPayment() throws Exception {
    var referrer = user("WORKER");
    var referred = user("WORKER");
    String admin = admin();
    String code =
        call("GET", "/api/v1/referral-code", referrer.token(), null, 200).get("code").asText();
    call("POST", "/api/v1/referrals", referred.token(), Map.of("code", code), 201);
    call("POST", "/api/v1/referrals", referred.token(), Map.of("code", code), 422);
    String plan = "";
    for (var p : call("GET", "/api/v1/plans", referred.token(), null, 200))
      if (p.get("code").asText().equals("SENTINEL_PRO")) plan = p.get("id").asText();
    String sub =
        call("POST", "/api/v1/subscriptions", referred.token(), Map.of("planId", plan), 201)
            .get("id")
            .asText();
    assertEquals(0, call("GET", "/api/v1/referral-rewards", referred.token(), null, 200).size());
    String payment =
        call("POST", "/api/v1/subscriptions/" + sub + "/payments", referred.token(), null, 201)
            .get("id")
            .asText();
    call(
        "POST",
        "/api/v1/payments/" + payment + "/confirm-offline",
        admin,
        Map.of("reference", "TEST-" + UUID.randomUUID()),
        200);
    var rewards = call("GET", "/api/v1/referral-rewards", referred.token(), null, 200);
    assertEquals(1, rewards.size());
    String reward = rewards.get(0).get("id").asText();
    call("POST", "/api/v1/referral-rewards/" + reward + "/redeem", referred.token(), null, 200);
    call("POST", "/api/v1/referral-rewards/" + reward + "/redeem", referred.token(), null, 422);
    assertEquals(1, call("GET", "/api/v1/referral-rewards", referrer.token(), null, 200).size());
  }

  @Test
  void freePlanContactLimitAndRiskModeration() throws Exception {
    var w = user("WORKER");
    var one = user("TRUSTED_CONTACT");
    var two = user("TRUSTED_CONTACT");
    call("POST", "/api/v1/trust-links", w.token(), Map.of("contactEmail", one.email()), 201);
    call("POST", "/api/v1/trust-links", w.token(), Map.of("contactEmail", two.email()), 422);
    var risk =
        call(
            "POST",
            "/api/v1/community/risk-zones",
            w.token(),
            Map.of(
                "description",
                "Road excavation",
                "riskLevel",
                "HIGH",
                "latitude",
                -12.1,
                "longitude",
                -77.1),
            201);
    assertEquals("PENDING", risk.get("status").asText());
    call(
        "POST",
        "/api/v1/community/risk-zones",
        w.token(),
        Map.of(
            "description", "Bad coords", "riskLevel", "HIGH", "latitude", 99, "longitude", -77.1),
        422);
  }
}
