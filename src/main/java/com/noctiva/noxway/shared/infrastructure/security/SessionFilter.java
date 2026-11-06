package com.noctiva.noxway.shared.infrastructure.security;

import com.noctiva.noxway.identity.domain.repositories.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SessionFilter extends OncePerRequestFilter {
  private final AuthSessionRepository sessions;
  private final AccountRepository accounts;

  public SessionFilter(AuthSessionRepository sessions, AccountRepository accounts) {
    this.sessions = sessions;
    this.accounts = accounts;
  }

  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ") && header.length() < 512) {
      var session =
          sessions.findByTokenHash(TokenHash.digest(header.substring(7))).stream()
              .filter(s -> s.active() && s.expiresAt().isAfter(Instant.now()))
              .findFirst();
      session
          .flatMap(s -> accounts.findById(s.accountId()))
          .filter(a -> a.active())
          .ifPresent(
              a ->
                  SecurityContextHolder.getContext()
                      .setAuthentication(
                          new UsernamePasswordAuthenticationToken(
                              new Actor(a.id(), a.role()),
                              null,
                              List.of(new SimpleGrantedAuthority("ROLE_" + a.role())))));
    }
    chain.doFilter(req, res);
  }
}
