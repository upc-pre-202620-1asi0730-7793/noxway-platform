package com.noctiva.noxway.shared.infrastructure.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Per-process abuse control. Multi-instance deployments should also rate-limit at the gateway. */
@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {
  private final int limit;

  public AuthRateLimitFilter(
      @org.springframework.beans.factory.annotation.Value("${noxway.auth-requests-per-minute:30}")
          int limit) {
    this.limit = limit;
  }

  private record Window(long begins, int count) {}

  private final ConcurrentHashMap<String, Window> attempts = new ConcurrentHashMap<>();

  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    if (req.getMethod().equals("POST") && req.getRequestURI().startsWith("/api/v1/auth/")) {
      long now = System.currentTimeMillis();
      if (attempts.size() > 10000)
        attempts.entrySet().removeIf(e -> now - e.getValue().begins() > 60000);
      String key = req.getRemoteAddr() + ":" + req.getRequestURI();
      var w =
          attempts.compute(
              key,
              (k, old) ->
                  old == null || now - old.begins() > 60000
                      ? new Window(now, 1)
                      : new Window(old.begins(), old.count() + 1));
      if (w.count() > limit) {
        res.setStatus(429);
        res.setHeader("Retry-After", "60");
        res.setContentType("application/problem+json");
        res.getWriter().write("{\"status\":429,\"detail\":\"Too many authentication requests\"}");
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
