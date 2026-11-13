package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts", schema = "users_network_schema")
public class AccountPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "phone", nullable = false)
  private String phone;

  @Column(name = "role", nullable = false)
  private String role;

  @Column(name = "shift_type")
  private String shiftType;

  @Column(name = "shift_start")
  private String shiftStart;

  @Column(name = "shift_end")
  private String shiftEnd;

  @Column(name = "time_zone", nullable = false)
  private String timeZone;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "active", nullable = false)
  private boolean active;

  public AccountPersistenceEntity() {}

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getShiftType() {
    return shiftType;
  }

  public void setShiftType(String shiftType) {
    this.shiftType = shiftType;
  }

  public String getShiftStart() {
    return shiftStart;
  }

  public void setShiftStart(String shiftStart) {
    this.shiftStart = shiftStart;
  }

  public String getShiftEnd() {
    return shiftEnd;
  }

  public void setShiftEnd(String shiftEnd) {
    this.shiftEnd = shiftEnd;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
