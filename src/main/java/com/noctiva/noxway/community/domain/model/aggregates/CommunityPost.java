package com.noctiva.noxway.community.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.noctiva.noxway.shared.domain.model.valueobjects.Coordinate;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CommunityPost extends AbstractDomainAggregateRoot<CommunityPost> {

  private final UUID id;
  private final UUID authorId;
  private final String title;
  private final String content;
  private final String district;
  private final String category;
  private final Double latitude;
  private final Double longitude;
  private final Instant createdAt;
  private final long version;

  private CommunityPost(
      UUID id,
      UUID authorId,
      String title,
      String content,
      String district,
      String category,
      Double latitude,
      Double longitude,
      Instant createdAt,
      long version) {
    this.id = id;
    this.authorId = authorId;
    this.title = title;
    this.content = content;
    this.district = district;
    this.category = category;
    this.latitude = latitude;
    this.longitude = longitude;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static CommunityPost create(UUID authorId, String title, String content, String district) {
    return create(authorId, title, content, district, null, null, null);
  }

  public static CommunityPost create(
      UUID authorId,
      String title,
      String content,
      String district,
      String category,
      Double latitude,
      Double longitude) {
    Objects.requireNonNull(authorId);
    require(title != null && !title.isBlank(), "Post title is required");
    require(content != null && !content.isBlank(), "Post content is required");
    String normalizedDistrict =
        (district == null || district.isBlank()) ? "Lima" : district.trim();
    require(normalizedDistrict.length() <= 100, "District must not exceed 100 characters");
    String normalizedCategory =
        (category == null || category.isBlank()) ? null : category.trim();
    if (normalizedCategory != null) {
      require(normalizedCategory.length() <= 80, "Post category must not exceed 80 characters");
    }
    if (latitude != null || longitude != null) {
      require(
          latitude != null && longitude != null,
          "Both latitude and longitude are required when location is provided");
      new Coordinate(latitude, longitude);
    }
    return new CommunityPost(
        UUID.randomUUID(),
        authorId,
        title.trim(),
        content.trim(),
        normalizedDistrict,
        normalizedCategory,
        latitude,
        longitude,
        Instant.now(),
        0L);
  }

  public static CommunityPost reconstitute(
      UUID id,
      UUID authorId,
      String title,
      String content,
      String district,
      Instant createdAt,
      long version) {
    return reconstitute(id, authorId, title, content, district, null, null, null, createdAt, version);
  }

  public static CommunityPost reconstitute(
      UUID id,
      UUID authorId,
      String title,
      String content,
      String district,
      String category,
      Double latitude,
      Double longitude,
      Instant createdAt,
      long version) {
    return new CommunityPost(
        id, authorId, title, content, district, category, latitude, longitude, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getAuthorId() {
    return authorId;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public String getDistrict() {
    return district;
  }

  public String getCategory() {
    return category;
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
