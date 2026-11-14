package com.noctiva.noxway.shared.infrastructure.configuration;

import com.noctiva.noxway.community.domain.model.aggregates.CommunityPost;
import com.noctiva.noxway.community.domain.model.aggregates.RiskZone;
import com.noctiva.noxway.community.domain.model.aggregates.ServiceOpeningInterval;
import com.noctiva.noxway.community.domain.model.aggregates.ServicePoint;
import com.noctiva.noxway.community.domain.repositories.CommunityPostRepository;
import com.noctiva.noxway.community.domain.repositories.RiskZoneRepository;
import com.noctiva.noxway.community.domain.repositories.ServiceOpeningIntervalRepository;
import com.noctiva.noxway.community.domain.repositories.ServicePointRepository;
import com.noctiva.noxway.commuting.domain.model.aggregates.Commute;
import com.noctiva.noxway.commuting.domain.repositories.CommuteRepository;
import com.noctiva.noxway.iam.domain.services.HashingService;
import com.noctiva.noxway.moderation.interfaces.acl.ModerationQueue;
import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.profiles.domain.repositories.AccountProfileRepository;
import com.noctiva.noxway.profiles.domain.repositories.NotificationPreferenceRepository;
import com.noctiva.noxway.profiles.domain.repositories.TrustLinkRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.CollectiveBenefit;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Plan;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Subscription;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.SubscriptionMember;
import com.noctiva.noxway.subscriptions.domain.repositories.CollectiveBenefitRepository;
import com.noctiva.noxway.subscriptions.domain.repositories.PlanRepository;
import com.noctiva.noxway.subscriptions.domain.repositories.SubscriptionMemberRepository;
import com.noctiva.noxway.subscriptions.domain.repositories.SubscriptionRepository;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import com.noctiva.noxway.wellness.domain.repositories.RestSessionRepository;
import com.noctiva.noxway.wellness.domain.repositories.SleepGoalRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Idempotent startup bootstrapper seeding reference plans, demo personas (Worker, Companion,
 * Moderator/Admin), community safety data, wellness history, and commuting history.
 */
@Component
public class BootstrapData implements ApplicationRunner {

  public static final UUID WORKER_PRIMARY_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000101");
  public static final UUID WORKER_SECONDARY_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000102");
  public static final UUID CONTACT_PRIMARY_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000201");
  public static final UUID CONTACT_SECONDARY_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000202");
  public static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000901");

  private final PlanRepository plans;
  private final AccountProfileRepository accounts;
  private final TrustLinkRepository trustLinks;
  private final NotificationPreferenceRepository notificationPrefs;
  private final SubscriptionRepository subscriptions;
  private final SubscriptionMemberRepository subscriptionMembers;
  private final CollectiveBenefitRepository benefits;
  private final ServicePointRepository servicePoints;
  private final ServiceOpeningIntervalRepository openingIntervals;
  private final RiskZoneRepository riskZones;
  private final CommunityPostRepository posts;
  private final ModerationQueue moderationQueue;
  private final SleepGoalRepository sleepGoals;
  private final RestSessionRepository restSessions;
  private final CommuteRepository commutes;
  private final HashingService hashingService;
  private final Clock clock;
  private final String adminEmail;
  private final String adminPassword;

  public BootstrapData(
      PlanRepository plans,
      AccountProfileRepository accounts,
      TrustLinkRepository trustLinks,
      NotificationPreferenceRepository notificationPrefs,
      SubscriptionRepository subscriptions,
      SubscriptionMemberRepository subscriptionMembers,
      CollectiveBenefitRepository benefits,
      ServicePointRepository servicePoints,
      ServiceOpeningIntervalRepository openingIntervals,
      RiskZoneRepository riskZones,
      CommunityPostRepository posts,
      ModerationQueue moderationQueue,
      SleepGoalRepository sleepGoals,
      RestSessionRepository restSessions,
      CommuteRepository commutes,
      HashingService hashingService,
      Clock clock,
      @Value("${noxway.bootstrap-admin-email:admin@noxway.test}") String adminEmail,
      @Value("${noxway.bootstrap-admin-password:TestingAdmin!2026}") String adminPassword) {
    this.plans = plans;
    this.accounts = accounts;
    this.trustLinks = trustLinks;
    this.notificationPrefs = notificationPrefs;
    this.subscriptions = subscriptions;
    this.subscriptionMembers = subscriptionMembers;
    this.benefits = benefits;
    this.servicePoints = servicePoints;
    this.openingIntervals = openingIntervals;
    this.riskZones = riskZones;
    this.posts = posts;
    this.moderationQueue = moderationQueue;
    this.sleepGoals = sleepGoals;
    this.restSessions = restSessions;
    this.commutes = commutes;
    this.hashingService = hashingService;
    this.clock = clock;
    this.adminEmail = adminEmail;
    this.adminPassword = adminPassword;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    seedPlans();
    seedAccountsAndTrustNetwork();
    seedBenefitsAndSampleSubscriptions();
    seedCommunityData();
    seedWellnessHistory();
    seedCommuteHistory();
  }

  private void seedPlans() {
    seedPlan("ESSENTIAL", "Esencial", "0.00", 1, 1);
    seedPlan("SENTINEL_PRO", "Centinela Pro", "12.00", 100, 1);
    seedPlan("FAMILY_CREW", "Cuadrilla Familiar", "25.00", 100, 5);
  }

  private void seedPlan(String code, String name, String price, int contacts, int workers) {
    if (plans.findByCode(code).isEmpty()) {
      plans.save(Plan.create(code, name, new BigDecimal(price), contacts, workers, true));
    }
  }

  private void seedAccountsAndTrustNetwork() {
    seedAccount(
        WORKER_PRIMARY_ID,
        "jorge.huaman@noxway.pe",
        "Jorge Luis Huamán",
        "+51987654321",
        "WORKER",
        "NIGHT",
        "22:00",
        "06:00",
        "America/Lima",
        null);
    seedAccount(
        WORKER_SECONDARY_ID,
        "carlos.mendoza@noxway.pe",
        "Carlos Mendoza",
        "+51987654322",
        "WORKER",
        "ROTATING",
        "20:00",
        "04:00",
        "America/Lima",
        null);
    seedAccount(
        CONTACT_PRIMARY_ID,
        "rosa.paredes@noxway.pe",
        "Rosa Elena Paredes Morales",
        "+51987111222",
        "TRUSTED_CONTACT",
        null,
        null,
        null,
        "America/Lima",
        null);
    seedAccount(
        CONTACT_SECONDARY_ID,
        "miguel.quispe@noxway.pe",
        "Miguel Ángel Quispe",
        "+51987333444",
        "TRUSTED_CONTACT",
        null,
        null,
        null,
        "America/Lima",
        null);

    if (adminEmail != null && !adminEmail.isBlank()) {
      if (adminPassword != null
          && !adminPassword.isBlank()
          && (adminPassword.length() < 12
              || adminPassword.getBytes(StandardCharsets.UTF_8).length > 72)) {
        throw new IllegalStateException(
            "Bootstrap password must be at least 12 characters and at most 72 UTF-8 bytes");
      }
      String normalizedAdmin = adminEmail.trim().toLowerCase(Locale.ROOT);
      var existingAdmin = accounts.findByEmail(normalizedAdmin);
      if (existingAdmin.isPresent() && !"ADMIN".equals(existingAdmin.get().getRole())) {
        throw new IllegalStateException("Bootstrap email already belongs to a non-admin account");
      }
      if (existingAdmin.isEmpty()) {
        String encoded =
            (adminPassword != null && !adminPassword.isBlank())
                ? hashingService.encode(adminPassword)
                : null;
        seedAccount(
            ADMIN_ID,
            normalizedAdmin,
            "Noxway Administrator",
            "+51900000901",
            "ADMIN",
            null,
            null,
            null,
            "America/Lima",
            encoded);
      }
    }

    if (trustLinks.findByWorkerId(WORKER_PRIMARY_ID).isEmpty()) {
      trustLinks.save(TrustLink.createActive(WORKER_PRIMARY_ID, CONTACT_PRIMARY_ID));
    }
    if (trustLinks.findByWorkerId(WORKER_SECONDARY_ID).isEmpty()) {
      trustLinks.save(TrustLink.createActive(WORKER_SECONDARY_ID, CONTACT_PRIMARY_ID));
      trustLinks.save(TrustLink.createPending(WORKER_SECONDARY_ID, CONTACT_SECONDARY_ID));
    }

    if (notificationPrefs.findByAccountId(CONTACT_PRIMARY_ID).isEmpty()) {
      notificationPrefs.save(
          NotificationPreference.create(
              CONTACT_PRIMARY_ID, "WHATSAPP", true, "22:00", "06:00", "America/Lima"));
      notificationPrefs.save(
          NotificationPreference.create(
              CONTACT_PRIMARY_ID, "EMAIL", true, "00:00", "23:59", "America/Lima"));
    }
  }

  private void seedAccount(
      UUID id,
      String email,
      String fullName,
      String phone,
      String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone,
      String passwordHash) {
    if (accounts.findById(id).isEmpty() && !accounts.existsByEmail(email)) {
      accounts.save(
          AccountProfile.create(
              id,
              email,
              fullName,
              phone,
              role,
              shiftType,
              shiftStart,
              shiftEnd,
              timeZone,
              passwordHash));
    }
  }

  private void seedBenefitsAndSampleSubscriptions() {
    if (benefits.findAll().isEmpty()) {
      benefits.save(
          CollectiveBenefit.create(
              "Tambo Nocturno 15%",
              "15% de descuento en café caliente y snacks entre 22:00 y 06:00 en tiendas aliadas.",
              "SENTINEL_PRO",
              true));
      benefits.save(
          CollectiveBenefit.create(
              "Primax Ruta Segura -S/ 0.40/gal",
              "Descuento directo por galón en combustible o recarga EV nocturna.",
              "SENTINEL_PRO",
              true));
      benefits.save(
          CollectiveBenefit.create(
              "Auxilio Vial Grúa 24/7",
              "Atención de emergencia mecánica y remolque urbano prioritario.",
              "FAMILY_CREW",
              true));
      benefits.save(
          CollectiveBenefit.create(
              "Botiquín Preventivo Inkafarma",
              "Descuento básico en analgésicos, vitaminas y primeros auxilios.",
              "ESSENTIAL",
              true));
    }

    if (subscriptions.findByWorkerId(WORKER_SECONDARY_ID).isEmpty()) {
      var familyPlan = plans.findByCode("FAMILY_CREW").stream().findFirst().orElse(null);
      if (familyPlan != null) {
        Instant now = clock.instant();
        var sharedSub =
            subscriptions.save(
                Subscription.create(
                    WORKER_SECONDARY_ID,
                    familyPlan.getId(),
                    "ACTIVE",
                    now.minus(Duration.ofDays(2)),
                    now.plus(Duration.ofDays(28))));
        subscriptionMembers.save(
            SubscriptionMember.invite(sharedSub.getId(), WORKER_PRIMARY_ID));
      }
    }
  }

  private void seedCommunityData() {
    if (servicePoints.findAll().isEmpty()) {
      seedServicePoint(
          WORKER_PRIMARY_ID,
          "Farmacia Universal 24h",
          "PHARMACY",
          -12.0545,
          -77.0365,
          "APPROVED",
          true);
      seedServicePoint(
          WORKER_PRIMARY_ID,
          "Estación Primax Javier Prado",
          "GAS_STATION",
          -12.0897,
          -77.0185,
          "APPROVED",
          true);
      seedServicePoint(
          WORKER_SECONDARY_ID,
          "Hospital Nacional Edgardo Rebagliati",
          "HOSPITAL",
          -12.0781,
          -77.0402,
          "APPROVED",
          true);
      seedServicePoint(
          WORKER_SECONDARY_ID,
          "Comisaría PNP Miraflores",
          "POLICE_STATION",
          -12.1190,
          -77.0294,
          "APPROVED",
          true);
      var pending =
          seedServicePoint(
              WORKER_SECONDARY_ID,
              "Grifo Repsol Av. La Marina 24h",
              "GAS_STATION",
              -12.0772,
              -77.0825,
              "PENDING",
              false);
      moderationQueue.submit(
          WORKER_SECONDARY_ID, "SERVICE", pending.getId(), "Nueva estación de servicio nocturna");
    }

    if (riskZones.findAll().isEmpty()) {
      riskZones.save(
          RiskZone.create(
              WORKER_PRIMARY_ID,
              "Iluminación pública intermitente en Av. Argentina cdra. 14 tras medianoche.",
              "HIGH",
              -12.0478,
              -77.0581,
              "APPROVED"));
      riskZones.save(
          RiskZone.create(
              WORKER_SECONDARY_ID,
              "Semáforo apagado en cruce Av. México con Vía Expresa de 00:00 a 05:00.",
              "MEDIUM",
              -12.0715,
              -77.0241,
              "APPROVED"));
      var pendingRisk =
          riskZones.save(
              RiskZone.create(
                  WORKER_PRIMARY_ID,
                  "Calzada en reparación sin señalización luminosa en Av. Nicolás Ayllón.",
                  "HIGH",
                  -12.0631,
                  -76.9992,
                  "PENDING"));
      moderationQueue.submit(
          WORKER_PRIMARY_ID, "RISK", pendingRisk.getId(), "Zona de riesgo reportada en turno noche");
    }

    if (posts.findAll().isEmpty()) {
      posts.save(
          CommunityPost.create(
              WORKER_PRIMARY_ID,
              "Operativo de serenazgo activo en Av. Arequipa",
              "Patrullaje reforzado entre las cuadras 18 y 35 hasta las 05:30 AM. Tránsito fluido para motos y autos.",
              "Lince",
              "Aviso de seguridad",
              -12.0849,
              -77.0358));
      posts.save(
          CommunityPost.create(
              WORKER_SECONDARY_ID,
              "Farmacia Universal atendiendo sin cola por ventanilla",
              "Ventanilla nocturna operativa con buena iluminación exterior y zona segura para estacionar 5 minutos.",
              "Lima Centro",
              "Reporte de local",
              -12.0548,
              -77.0366));
      posts.save(
          CommunityPost.create(
              WORKER_PRIMARY_ID,
              "Precaución por neblina densa en Costa Verde",
              "Visibilidad reducida desde Bajada Armendáriz hasta Magdalena; usar luces neblineras y bajar velocidad.",
              "Miraflores",
              "Aviso de seguridad",
              -12.1268,
              -77.0369));
    }
  }

  private ServicePoint seedServicePoint(
      UUID reportedBy,
      String name,
      String type,
      double lat,
      double lon,
      String status,
      boolean is24h) {
    var saved =
        servicePoints.save(
            ServicePoint.create(reportedBy, name, type, lat, lon, "America/Lima", status));
    for (int day = 1; day <= 7; day++) {
      if (is24h) {
        openingIntervals.save(
            ServiceOpeningInterval.create(saved.getId(), day, "00:00", "00:00", false, true));
      } else {
        openingIntervals.save(
            ServiceOpeningInterval.create(saved.getId(), day, "21:00", "06:00", true, false));
      }
    }
    return saved;
  }

  private void seedWellnessHistory() {
    if (sleepGoals.findByWorkerId(WORKER_PRIMARY_ID).isEmpty()) {
      sleepGoals.save(SleepGoal.create(WORKER_PRIMARY_ID, 450));
    }
    if (restSessions.findByWorkerId(WORKER_PRIMARY_ID).isEmpty()) {
      Instant now = clock.instant();
      for (int daysAgo = 5; daysAgo >= 1; daysAgo--) {
        Instant start = now.minus(daysAgo, ChronoUnit.DAYS).minus(Duration.ofHours(9));
        Instant end = start.plus(Duration.ofMinutes(420 + (daysAgo % 3) * 20L));
        int quality = 3 + (daysAgo % 3);
        restSessions.save(
            RestSession.create(
                WORKER_PRIMARY_ID,
                start,
                end,
                quality,
                daysAgo % 2 == 0
                    ? "Descanso diurno con antifaz y habitación oscura."
                    : "Recuperación continua posterior al turno."));
      }
    }
  }

  private void seedCommuteHistory() {
    if (commutes.findByWorkerId(WORKER_PRIMARY_ID).isEmpty()) {
      Instant now = clock.instant();
      Instant start = now.minus(Duration.ofHours(5));
      Instant eta = start.plus(Duration.ofMinutes(35));
      var completed =
          Commute.start(
              WORKER_PRIMARY_ID,
              "Hospital Nacional Rebagliati, Jesús María",
              "Av. Brasil cdra. 24, Pueblo Libre",
              "MOTORCYCLE",
              start,
              eta,
              15);
      completed.arrive(start.plus(Duration.ofMinutes(32)));
      commutes.save(completed);
    }
  }
}
