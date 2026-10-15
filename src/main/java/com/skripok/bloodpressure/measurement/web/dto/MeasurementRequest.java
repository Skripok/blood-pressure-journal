package com.skripok.bloodpressure.measurement.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MeasurementRequest(
        Long personId,
        LocalDateTime measuredAt,
        Integer systolic,
        Integer diastolic,
        Integer pulse,
        String note,
        BigDecimal heightCm,
        BigDecimal weightKg
) {
}