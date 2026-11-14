package com.noctiva.noxway.iam.domain.services;

/**
 * Domain port for password hashing (structured for future IAM implementation).
 */
public interface HashingService {
  String encode(String rawPassword);

  boolean matches(String rawPassword, String encodedPassword);
}
