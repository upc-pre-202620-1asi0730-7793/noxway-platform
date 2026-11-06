package com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts", schema = "users_network_schema")
public class AccountJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "email", nullable = false, unique = true)
  public String email;

  @Column(name = "full_name", nullable = false)
  public String fullName;

  @Column(name = "phone", nullable = false)
  public String phone;

  @Column(name = "role", nullable = false)
  public String role;

  @Column(name = "shift_type", nullable = true)
  public String shiftType;

  @Column(name = "shift_start", nullable = true)
  public String shiftStart;

  @Column(name = "shift_end", nullable = true)
  public String shiftEnd;

  @Column(name = "time_zone", nullable = false)
  public String timeZone;

  @Column(name = "password_hash", nullable = false)
  public String passwordHash;

  @Column(name = "active", nullable = false)
  public boolean active;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
