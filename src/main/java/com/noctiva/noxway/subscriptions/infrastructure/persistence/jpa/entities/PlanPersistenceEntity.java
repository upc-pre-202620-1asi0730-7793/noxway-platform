package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "plans", schema = "subscription_schema")
public class PlanPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "monthly_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal monthlyPrice;

  @Column(name = "max_contacts", nullable = false)
  private int maxContacts;

  @Column(name = "max_workers", nullable = false)
  private int maxWorkers;

  @Column(name = "active", nullable = false)
  private boolean active;

  public PlanPersistenceEntity() {}

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getMonthlyPrice() {
    return monthlyPrice;
  }

  public void setMonthlyPrice(BigDecimal monthlyPrice) {
    this.monthlyPrice = monthlyPrice;
  }

  public int getMaxContacts() {
    return maxContacts;
  }

  public void setMaxContacts(int maxContacts) {
    this.maxContacts = maxContacts;
  }

  public int getMaxWorkers() {
    return maxWorkers;
  }

  public void setMaxWorkers(int maxWorkers) {
    this.maxWorkers = maxWorkers;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
