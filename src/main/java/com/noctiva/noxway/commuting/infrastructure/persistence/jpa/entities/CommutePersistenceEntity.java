package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "commutes", schema = "commute_incident_schema")
public class CommutePersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "origin", nullable = false)
  private String origin;

  @Column(name = "destination", nullable = false)
  private String destination;

  @Column(name = "transport", nullable = false)
  private String transport;

  @Column(name = "start_time", nullable = false)
  private Instant startTime;

  @Column(name = "estimated_arrival_time", nullable = false)
  private Instant estimatedArrivalTime;

  @Column(name = "tolerance_minutes", nullable = false)
  private int toleranceMinutes;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "arrival_time")
  private Instant arrivalTime;

  @Column(name = "delay_until")
  private Instant delayUntil;

  public CommutePersistenceEntity() {}

  public UUID getWorkerId() {
    return workerId;
  }

  public void setWorkerId(UUID workerId) {
    this.workerId = workerId;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public String getTransport() {
    return transport;
  }

  public void setTransport(String transport) {
    this.transport = transport;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  public Instant getEstimatedArrivalTime() {
    return estimatedArrivalTime;
  }

  public void setEstimatedArrivalTime(Instant estimatedArrivalTime) {
    this.estimatedArrivalTime = estimatedArrivalTime;
  }

  public int getToleranceMinutes() {
    return toleranceMinutes;
  }

  public void setToleranceMinutes(int toleranceMinutes) {
    this.toleranceMinutes = toleranceMinutes;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(Instant arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public Instant getDelayUntil() {
    return delayUntil;
  }

  public void setDelayUntil(Instant delayUntil) {
    this.delayUntil = delayUntil;
  }
}
