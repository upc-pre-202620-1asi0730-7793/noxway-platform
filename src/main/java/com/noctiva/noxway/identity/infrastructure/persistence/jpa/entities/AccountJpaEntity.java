package com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "email", nullable = false, unique = true, length = 254)
  public String email;

  @Column(name = "full_name", nullable = false, length = 150)
  public String fullName;

  @Column(name = "phone", nullable = false, length = 30)
  public String phone;

  @Column(name = "role", nullable = false, length = 32)
  public String role;

  @Column(name = "shift_type", nullable = true, length = 16)
  public String shiftType;

  @Column(name = "shift_start", nullable = true, length = 5)
  public String shiftStart;

  @Column(name = "shift_end", nullable = true, length = 5)
  public String shiftEnd;

  @Column(name = "time_zone", nullable = false, length = 100)
  public String timeZone;

  @Column(name = "password_hash", nullable = false, length = 72)
  public String passwordHash;

  @Column(name = "active", nullable = false)
  public boolean active;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
