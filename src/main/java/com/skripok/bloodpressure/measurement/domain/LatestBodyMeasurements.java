package com.skripok.bloodpressure.measurement.domain;

import java.math.BigDecimal;

public record LatestBodyMeasurements(BigDecimal heightCm, BigDecimal weightKg) {

}