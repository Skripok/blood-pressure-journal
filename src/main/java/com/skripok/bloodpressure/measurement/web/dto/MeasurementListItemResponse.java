package com.skripok.bloodpressure.measurement.web.dto;

import java.time.LocalDateTime;

public record MeasurementListItemResponse(
        Long id,
        Long personId,
        LocalDateTime measuredAt,
        Integer systolic,
        Integer diastolic,
        Integer pulse
) {
}