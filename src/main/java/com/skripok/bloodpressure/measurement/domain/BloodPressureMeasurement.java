package com.skripok.bloodpressure.measurement.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BloodPressureMeasurement {

    private Long id;
    private Long personId;
    private LocalDateTime measuredAt;
    private Integer systolic;
    private Integer diastolic;
    private Integer pulse;
    private String note;
    private BigDecimal heightCm;
    private BigDecimal weightKg;

    public BloodPressureMeasurement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }


    public void validate() {
        if (systolic == null || diastolic == null) {
            throw new IllegalArgumentException(
                    "Systolic and diastolic pressure are required");
        }

        if (systolic <= diastolic) {
            throw new IllegalArgumentException(
                    "Systolic pressure must be greater than diastolic pressure");
        }
    }
}