package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "plans")
public class PlanJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "code", nullable = false, unique = true, length = 100)
  public String code;

  @Column(name = "name", nullable = false, length = 150)
  public String name;

  @Column(name = "monthly_price", nullable = false, precision = 12, scale = 2)
  public BigDecimal monthlyPrice;

  @Column(name = "max_contacts", nullable = false)
  public int maxContacts;

  @Column(name = "max_workers", nullable = false)
  public int maxWorkers;

  @Column(name = "active", nullable = false)
  public boolean active;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
