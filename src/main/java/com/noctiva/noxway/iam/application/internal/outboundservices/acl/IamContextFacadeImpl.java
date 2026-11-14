package com.noctiva.noxway.iam.application.internal.outboundservices.acl;

import com.noctiva.noxway.iam.domain.model.valueobjects.Actor;
import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Resolves the active actor context. While IAM is deferred ({@code app.security.enabled=false}),
 * supports optional {@code X-Actor-Id} header lookup for previewing worker/companion/admin personas
 * and falls back cleanly to the default seeded worker profile.
 */
@Service
public class IamContextFacadeImpl implements IamContextFacade {

  private final boolean securityEnabled;
  private final UUID defaultWorkerId;
  private final ProfileDirectoryFacade profileDirectory;

  public IamContextFacadeImpl(
      @Value("${app.security.enabled:false}") boolean securityEnabled,
      @Value("${noxway.default-worker-id:00000000-0000-0000-0000-000000000101}")
          UUID defaultWorkerId,
      ProfileDirectoryFacade profileDirectory) {
    this.securityEnabled = securityEnabled;
    this.defaultWorkerId = defaultWorkerId;
    this.profileDirectory = profileDirectory;
  }

  @Override
  public Actor resolveCurrentActor() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof Actor actor) {
      return actor;
    }
    if (!securityEnabled) {
      var attrs = RequestContextHolder.getRequestAttributes();
      if (attrs instanceof ServletRequestAttributes servletAttrs) {
        String header = servletAttrs.getRequest().getHeader("X-Actor-Id");
        if (header != null && !header.isBlank()) {
          try {
            UUID requestedId = UUID.fromString(header.trim());
            return profileDirectory
                .findById(requestedId)
                .filter(ProfileDirectoryFacade.AccountSummary::active)
                .map(a -> new Actor(a.id(), a.role()))
                .orElseGet(() -> fallbackActor());
          } catch (IllegalArgumentException ignored) {
            // Fall through to default actor when header is malformed
          }
        }
      }
      return fallbackActor();
    }
    throw new IllegalStateException("Authenticated actor principal required when IAM is enabled");
  }

  private Actor fallbackActor() {
    return profileDirectory
        .findById(defaultWorkerId)
        .filter(ProfileDirectoryFacade.AccountSummary::active)
        .map(a -> new Actor(a.id(), a.role()))
        .orElseGet(() -> new Actor(defaultWorkerId, "WORKER"));
  }

  @Override
  public boolean isSecurityEnabled() {
    return securityEnabled;
  }

  @Override
  public UUID defaultWorkerId() {
    return defaultWorkerId;
  }
}
