package com.skripok.bloodpressure.measurement.service;

import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.domain.LatestBodyMeasurements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface BloodPressureMeasurementService {

    BloodPressureMeasurement create(BloodPressureMeasurement measurement);

    BloodPressureMeasurement findById(Long id);

    Page<BloodPressureMeasurement> findByPersonId(Long personId, Pageable pageable);

    BloodPressureMeasurement update(Long id, BloodPressureMeasurement measurement);

    void delete(Long id);

    LatestBodyMeasurements findLatestBodyMeasurements(Long personId);

    List<BloodPressureMeasurement> findByPersonIdAndPeriod(Long personId, LocalDateTime from, LocalDateTime to);
}