package com.noctiva.noxway;

import static org.junit.jupiter.api.Assertions.*;

import com.noctiva.noxway.identity.application.IdentityApplicationService;
import com.noctiva.noxway.identity.application.PasswordRecoveryService;
import com.noctiva.noxway.identity.application.commands.IdentityCommands;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(PasswordRecoveryIntegrationTest.MailConfig.class)
class PasswordRecoveryIntegrationTest {
  static final List<SimpleMailMessage> messages = new java.util.concurrent.CopyOnWriteArrayList<>();

  @TestConfiguration
  static class MailConfig {
    @Bean
    JavaMailSender mail() {
      return new JavaMailSenderImpl() {
        @Override
        public void send(SimpleMailMessage message) {
          messages.add(message);
        }
      };
    }
  }

  @Autowired PasswordRecoveryService recovery;
  @Autowired IdentityApplicationService identity;

  @Test
  void recoveryIsSingleUseAndChangesCredentials() {
    String email = UUID.randomUUID() + "@test.local";
    identity.register(
        new IdentityCommands.Register(
            email,
            "OldTestPassword!2026",
            "Recovery User",
            "987654321",
            "WORKER",
            "DAY",
            "08:00",
            "17:00",
            "America/Lima"));
    recovery.request(email);
    var msg =
        messages.stream()
            .filter(m -> Arrays.asList(m.getTo()).contains(email))
            .findFirst()
            .orElseThrow();
    String token = msg.getText().split(": ", 2)[1].split("\n", 2)[0];
    recovery.reset(token, "NewTestPassword!2026");
    assertThrows(RuntimeException.class, () -> recovery.reset(token, "OtherPassword!2026"));
    assertThrows(
        RuntimeException.class,
        () -> identity.login(new IdentityCommands.Login(email, "OldTestPassword!2026")));
    assertNotNull(
        identity.login(new IdentityCommands.Login(email, "NewTestPassword!2026")).accessToken());
  }
}
