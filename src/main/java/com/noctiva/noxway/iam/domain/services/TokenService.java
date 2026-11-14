package com.noctiva.noxway.iam.domain.services;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for token issuance and verification (structured for future IAM implementation).
 */
public interface TokenService {
  String generateToken(UUID accountId, String role);

  Optional<UUID> extractAccountIdIfValid(String token);
}
