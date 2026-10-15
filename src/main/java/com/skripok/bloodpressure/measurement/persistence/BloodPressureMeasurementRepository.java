package com.skripok.bloodpressure.measurement.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloodPressureMeasurementRepository
        extends JpaRepository<BloodPressureMeasurementEntity, Long> {

    Page<BloodPressureMeasurementEntity> findByPerson_Id(Long personId, Pageable pageable);

    Optional<BloodPressureMeasurementEntity>
    findFirstByPerson_IdAndHeightCmIsNotNullOrderByMeasuredAtDesc(Long personId);

    Optional<BloodPressureMeasurementEntity>
    findFirstByPerson_IdAndWeightKgIsNotNullOrderByMeasuredAtDesc(Long personId);
}