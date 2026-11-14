package com.noctiva.noxway.shared.domain.model.valueobjects;

import static com.noctiva.noxway.shared.domain.Rules.require;

/**
 * Geographic coordinate value object enforcing WGS84 latitude and longitude bounds.
 *
 * @param latitude latitude in range [-90, 90]
 * @param longitude longitude in range [-180, 180]
 */
public record Coordinate(double latitude, double longitude) {
  public Coordinate {
    require(Double.isFinite(latitude) && latitude >= -90.0 && latitude <= 90.0, "Invalid latitude");
    require(
        Double.isFinite(longitude) && longitude >= -180.0 && longitude <= 180.0,
        "Invalid longitude");
  }
}
