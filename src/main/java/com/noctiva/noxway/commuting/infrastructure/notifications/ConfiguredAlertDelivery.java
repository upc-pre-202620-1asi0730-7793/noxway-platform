package com.noctiva.noxway.commuting.infrastructure.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noctiva.noxway.commuting.domain.services.AlertDeliveryGateway;
import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class ConfiguredAlertDelivery implements AlertDeliveryGateway {
  private final ObjectProvider<JavaMailSender> mail;
  private final ObjectMapper json;
  private final String from, url, secret;

  public ConfiguredAlertDelivery(
      ObjectProvider<JavaMailSender> m,
      ObjectMapper j,
      @Value("${noxway.mail-from:noreply@example.invalid}") String f,
      @Value("${noxway.delivery-webhook-url:}") String u,
      @Value("${noxway.delivery-webhook-secret:}") String s) {
    mail = m;
    json = j;
    from = f;
    url = u;
    secret = s;
  }

  public Result deliver(
      String channel,
      String email,
      String phone,
      String message,
      String eventId,
      boolean priority) {
    try {
      if (channel.equals("EMAIL")) {
        var sender = mail.getIfAvailable();
        if (sender == null) return Result.UNCONFIGURED;
        var msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(email);
        msg.setSubject((priority ? "[Priority] " : "") + "Noxway commute alert");
        msg.setText(message + "\nEvent: " + eventId);
        sender.send(msg);
        return Result.DELIVERED;
      }
      if (!channel.equals("SMS") && !channel.equals("WHATSAPP")) return Result.UNCONFIGURED;
      if (url.isBlank() || secret.isBlank()) return Result.UNCONFIGURED;
      URI uri = URI.create(url);
      if (!uri.getScheme().equals("https")) return Result.FAILED;
      var body =
          json.writeValueAsString(
              Map.of(
                  "channel",
                  channel,
                  "recipient",
                  phone,
                  "message",
                  message,
                  "eventId",
                  eventId,
                  "priority",
                  priority));
      var request =
          HttpRequest.newBuilder(uri)
              .timeout(Duration.ofSeconds(10))
              .header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + secret)
              .header("Idempotency-Key", eventId)
              .POST(HttpRequest.BodyPublishers.ofString(body))
              .build();
      try (var client =
          HttpClient.newBuilder()
              .connectTimeout(Duration.ofSeconds(5))
              .followRedirects(HttpClient.Redirect.NEVER)
              .build()) {
        int code = client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
        return code >= 200 && code < 300 ? Result.DELIVERED : Result.FAILED;
      }
    } catch (Exception e) {
      if (e instanceof InterruptedException) Thread.currentThread().interrupt();
      return Result.FAILED;
    }
  }
}
