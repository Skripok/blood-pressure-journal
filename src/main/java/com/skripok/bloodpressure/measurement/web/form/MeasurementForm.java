package com.skripok.bloodpressure.measurement.web.form;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MeasurementForm {

    private Long personId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime measuredAt;

    private Integer systolic;
    private Integer diastolic;
    private Integer pulse;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String note;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}