package com.skripok.bloodpressure.measurement.service;

import com.skripok.bloodpressure.common.exception.ResourceNotFoundException;
import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.domain.LatestBodyMeasurements;
import com.skripok.bloodpressure.measurement.mapper.BloodPressureMeasurementMapper;
import com.skripok.bloodpressure.measurement.persistence.BloodPressureMeasurementEntity;
import com.skripok.bloodpressure.measurement.persistence.BloodPressureMeasurementRepository;
import com.skripok.bloodpressure.person.persistence.PersonEntity;
import com.skripok.bloodpressure.person.persistence.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class BloodPressureMeasurementServiceImpl implements BloodPressureMeasurementService {

    private final BloodPressureMeasurementRepository measurementRepository;
    private final PersonRepository personRepository;
    private final BloodPressureMeasurementMapper measurementMapper;

    public BloodPressureMeasurementServiceImpl (
            BloodPressureMeasurementRepository measurementRepository,
            PersonRepository personRepository,
            BloodPressureMeasurementMapper measurementMapper) {

        this.measurementRepository = measurementRepository;
        this.personRepository = personRepository;
        this.measurementMapper = measurementMapper;
    }

    @Override
    @Transactional
    public BloodPressureMeasurement create(BloodPressureMeasurement measurement) {

        measurement.validate();

        final PersonEntity person = personRepository.findById(measurement.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found: " + measurement.getPersonId()));

        final BloodPressureMeasurementEntity entity =
                measurementMapper.toEntity(measurement);

        entity.setPerson(person);

        final BloodPressureMeasurementEntity savedEntity =
                measurementRepository.save(entity);

        return measurementMapper.toDomain(savedEntity);
    }

    @Override
    public BloodPressureMeasurement findById(Long id) {
        return measurementRepository.findById(id)
                .map(measurementMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Measurement not found: " + id));
    }

    @Override
    public Page<BloodPressureMeasurement> findByPersonId(
            Long personId,
            Pageable pageable) {

        return measurementRepository
                .findByPerson_Id(personId, pageable)
                .map(measurementMapper::toDomain);
    }

    @Override
    @Transactional
    public BloodPressureMeasurement update(
            Long id,
            BloodPressureMeasurement measurement) {

        measurement.validate();

        final BloodPressureMeasurementEntity entity = measurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Measurement not found: " + id));

        if (!entity.getPerson().getId().equals(measurement.getPersonId())) {
            throw new IllegalArgumentException(
                    "Cannot change the person of an existing measurement");
        }

        entity.setMeasuredAt(measurement.getMeasuredAt());
        entity.setSystolic(measurement.getSystolic());
        entity.setDiastolic(measurement.getDiastolic());
        entity.setPulse(measurement.getPulse());
        entity.setNote(measurement.getNote());
        entity.setHeightCm(measurement.getHeightCm());
        entity.setWeightKg(measurement.getWeightKg());

        return measurementMapper.toDomain(
                measurementRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {

        final BloodPressureMeasurementEntity entity = measurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Measurement not found: " + id));

        measurementRepository.delete(entity);
    }

    @Override
    public LatestBodyMeasurements findLatestBodyMeasurements(Long personId) {
        final BigDecimal heightCm = measurementRepository
                .findFirstByPerson_IdAndHeightCmIsNotNullOrderByMeasuredAtDesc(personId)
                .map(BloodPressureMeasurementEntity::getHeightCm)
                .orElse(null);

        final BigDecimal weightKg = measurementRepository
                .findFirstByPerson_IdAndWeightKgIsNotNullOrderByMeasuredAtDesc(personId)
                .map(BloodPressureMeasurementEntity::getWeightKg)
                .orElse(null);

        return new LatestBodyMeasurements(heightCm, weightKg);
    }

    @Override
    public List<BloodPressureMeasurement> findByPersonIdAndPeriod(Long personId, LocalDateTime from, LocalDateTime to) {

        if (from == null || to == null || !from.isBefore(to)) {
            throw new IllegalArgumentException("Invalid measurement period");
        }

        return measurementRepository
                .findByPerson_IdAndMeasuredAtGreaterThanEqualAndMeasuredAtLessThanOrderByMeasuredAtAsc(
                        personId,
                        from,
                        to
                )
                .stream()
                .map(measurementMapper::toDomain)
                .toList();
    }
}