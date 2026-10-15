package com.skripok.bloodpressure.measurement.persistence;

import com.skripok.bloodpressure.person.persistence.PersonEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_pressure_measurement")
public class BloodPressureMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(nullable = false)
    private Integer systolic;

    @Column(nullable = false)
    private Integer diastolic;

    @Column
    private Integer pulse;

    @Column(length = 1000)
    private String note;

    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public BloodPressureMeasurementEntity() {
    }

    public Long getId() {
        return id;
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

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }
}