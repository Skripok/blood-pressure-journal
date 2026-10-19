package com.skripok.bloodpressure.measurement.web.dto;

import java.time.LocalDateTime;

public record ChartMeasurementDto(
        LocalDateTime measuredAt,
        Integer systolic,
        Integer diastolic,
        Integer pulse
) {
}