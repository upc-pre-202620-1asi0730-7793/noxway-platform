package com.noctiva.noxway.iam.infrastructure.hashing.bcrypt;

import com.noctiva.noxway.iam.domain.services.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt adapter implementing {@link HashingService} for structural IAM completeness.
 */
@Service
public class BCryptHashingServiceImpl implements HashingService {

  private final PasswordEncoder passwordEncoder;

  public BCryptHashingServiceImpl(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
