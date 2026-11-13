package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "risk_zones", schema = "community_data_schema")
public class RiskZonePersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "reported_by", nullable = false)
  private UUID reportedBy;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "risk_level", nullable = false)
  private String riskLevel;

  @Column(name = "latitude", nullable = false)
  private double latitude;

  @Column(name = "longitude", nullable = false)
  private double longitude;

  @Column(name = "status", nullable = false)
  private String status;

  public RiskZonePersistenceEntity() {}

  public UUID getReportedBy() {
    return reportedBy;
  }

  public void setReportedBy(UUID reportedBy) {
    this.reportedBy = reportedBy;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getRiskLevel() {
    return riskLevel;
  }

  public void setRiskLevel(String riskLevel) {
    this.riskLevel = riskLevel;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
