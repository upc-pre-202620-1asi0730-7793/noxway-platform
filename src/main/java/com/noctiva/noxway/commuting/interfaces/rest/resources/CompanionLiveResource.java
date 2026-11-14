package com.noctiva.noxway.commuting.interfaces.rest.resources;

import java.util.List;
import java.util.UUID;

public record CompanionLiveResource(
    UUID workerId,
    String workerName,
    String workerPhone,
    String shiftType,
    String shiftStart,
    String shiftEnd,
    CommuteResource activeCommute,
    TelemetryPingResource latestPing,
    List<SafetyIncidentResource> openIncidents,
    int recentAlertCount) {}
