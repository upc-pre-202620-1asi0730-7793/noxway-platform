package com.noctiva.noxway.shared.infrastructure.security;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public record Actor(UUID id, String role) {
  public static Actor current() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !(a.getPrincipal() instanceof Actor actor))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
    return actor;
  }

  public boolean privileged() {
    return role.equals("ADMIN") || role.equals("MODERATOR");
  }

  public void own(UUID owner) {
    if (!id.equals(owner))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Resource belongs to another user");
  }

  public void worker() {
    if (!role.equals("WORKER"))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Worker role required");
  }

  public void moderator() {
    if (!privileged())
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Moderator role required");
  }
}
