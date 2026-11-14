package com.noctiva.noxway.community.application.internal.outboundservices.acl;

import com.noctiva.noxway.community.domain.repositories.RiskZoneRepository;
import com.noctiva.noxway.community.domain.repositories.ServicePointRepository;
import com.noctiva.noxway.community.interfaces.acl.CommunityModeration;
import com.noctiva.noxway.shared.domain.RuleViolation;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommunityModerationImpl implements CommunityModeration {

  private final ServicePointRepository serviceRepository;
  private final RiskZoneRepository riskRepository;

  public CommunityModerationImpl(
      ServicePointRepository serviceRepository, RiskZoneRepository riskRepository) {
    this.serviceRepository = serviceRepository;
    this.riskRepository = riskRepository;
  }

  @Override
  public void decide(String targetType, UUID targetId, boolean approved) {
    if ("SERVICE".equals(targetType)) {
      var service =
          serviceRepository
              .findById(targetId)
              .orElseThrow(() -> new NoSuchElementException("Service not found"));
      service.decide(approved);
      serviceRepository.save(service);
    } else if ("RISK".equals(targetType)) {
      var risk =
          riskRepository
              .findById(targetId)
              .orElseThrow(() -> new NoSuchElementException("Risk zone not found"));
      risk.decide(approved);
      riskRepository.save(risk);
    } else {
      throw new RuleViolation("Unsupported moderation target");
    }
  }

  @Override
  public boolean exists(String type, UUID id) {
    return switch (type) {
      case "SERVICE" -> serviceRepository.findById(id).isPresent();
      case "RISK" -> riskRepository.findById(id).isPresent();
      default -> false;
    };
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<TargetSummary> summarize(String targetType, UUID targetId) {
    if ("SERVICE".equals(targetType)) {
      return serviceRepository
          .findById(targetId)
          .map(s -> new TargetSummary(s.getId(), "SERVICE", s.getName(), s.getStatus()));
    }
    if ("RISK".equals(targetType)) {
      return riskRepository
          .findById(targetId)
          .map(r -> new TargetSummary(r.getId(), "RISK", r.getDescription(), r.getStatus()));
    }
    return Optional.empty();
  }
}
