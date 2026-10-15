package com.skripok.bloodpressure.measurement.service;

import com.skripok.bloodpressure.common.exception.ResourceNotFoundException;
import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.persistence.BloodPressureMeasurementEntity;
import com.skripok.bloodpressure.measurement.persistence.BloodPressureMeasurementRepository;
import com.skripok.bloodpressure.person.persistence.PersonRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.domain.enums.Gender;
import com.skripok.bloodpressure.person.service.PersonService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
@Transactional
class BloodPressureMeasurementServiceImplTest {

    @Autowired
    private BloodPressureMeasurementService measurementService;

    @Autowired
    private BloodPressureMeasurementRepository measurementRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PersonService personService;

    private Long createTestPerson() {
        final Person person = new Person(
                null,
                "John",
                "Smith",
                "Michael",
                LocalDate.of(1985, 5, 15),
                Gender.MALE,
                true,
                null,
                null
        );

        return personService.create(person).getId();
    }

    private BloodPressureMeasurement createTestMeasurement(Long personId, LocalDateTime measuredAt) {
        final BloodPressureMeasurement measurement = new BloodPressureMeasurement();

        measurement.setPersonId(personId);
        measurement.setMeasuredAt(measuredAt);
        measurement.setSystolic(125);
        measurement.setDiastolic(80);
        measurement.setPulse(72);
        measurement.setNote("Test measurement");
        measurement.setHeightCm(new BigDecimal("180.00"));
        measurement.setWeightKg(new BigDecimal("85.50"));

        return measurementService.create(measurement);
    }

    @Test
    void create_shouldPersistMeasurement() {

        // Test data.
        final Long personId = createTestPerson();;

        final LocalDateTime measuredAt = LocalDateTime.of(2026, 10, 2, 12, 30);
        final Integer systolic = 125;
        final Integer diastolic = 80;
        final Integer pulse = 72;
        final String note = "Test measurement";
        final BigDecimal heightCm = new BigDecimal("180.00");
        final BigDecimal weightKg = new BigDecimal("85.50");

        final BloodPressureMeasurement measurement = new BloodPressureMeasurement();
        measurement.setPersonId(personId);
        measurement.setMeasuredAt(measuredAt);
        measurement.setSystolic(systolic);
        measurement.setDiastolic(diastolic);
        measurement.setPulse(pulse);
        measurement.setNote(note);
        measurement.setHeightCm(heightCm);
        measurement.setWeightKg(weightKg);

        // Execute.
        final BloodPressureMeasurement createdMeasurement =
                measurementService.create(measurement);

        // Verify returned domain object.
        assertNotNull(createdMeasurement);
        assertNotNull(createdMeasurement.getId());

        assertAll(
                () -> assertEquals(personId, createdMeasurement.getPersonId()),
                () -> assertEquals(measuredAt, createdMeasurement.getMeasuredAt()),
                () -> assertEquals(systolic, createdMeasurement.getSystolic()),
                () -> assertEquals(diastolic, createdMeasurement.getDiastolic()),
                () -> assertEquals(pulse, createdMeasurement.getPulse()),
                () -> assertEquals(note, createdMeasurement.getNote()),
                () -> assertEquals(heightCm, createdMeasurement.getHeightCm()),
                () -> assertEquals(weightKg, createdMeasurement.getWeightKg())
        );

        // Persist pending changes and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        // Verify persisted entity.
        final BloodPressureMeasurementEntity savedEntity = measurementRepository
                .findById(createdMeasurement.getId())
                .orElseThrow();

        assertAll(
                () -> assertEquals(createdMeasurement.getId(), savedEntity.getId()),
                () -> assertEquals(personId, savedEntity.getPerson().getId()),
                () -> assertEquals(measuredAt, savedEntity.getMeasuredAt()),
                () -> assertEquals(systolic, savedEntity.getSystolic()),
                () -> assertEquals(diastolic, savedEntity.getDiastolic()),
                () -> assertEquals(pulse, savedEntity.getPulse()),
                () -> assertEquals(note, savedEntity.getNote()),
                () -> assertEquals(heightCm, savedEntity.getHeightCm()),
                () -> assertEquals(weightKg, savedEntity.getWeightKg())
        );
    }

    @Test
    void findById_shouldReturnPersistedMeasurement() {

        // Test data.
        final Long personId = createTestPerson();
        final LocalDateTime measuredAt = LocalDateTime.of(2026, 10, 2, 12, 30);
        final BloodPressureMeasurement createdMeasurement = createTestMeasurement(personId, measuredAt);

        // Persist pending changes and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        // Execute.
        final BloodPressureMeasurement foundMeasurement =
                measurementService.findById(createdMeasurement.getId());

        // Verify.
        assertNotNull(foundMeasurement);

        assertAll(
                () -> assertEquals(createdMeasurement.getId(), foundMeasurement.getId()),
                () -> assertEquals(personId, foundMeasurement.getPersonId()),
                () -> assertEquals(measuredAt, foundMeasurement.getMeasuredAt()),
                () -> assertEquals(125, foundMeasurement.getSystolic()),
                () -> assertEquals(80, foundMeasurement.getDiastolic()),
                () -> assertEquals(72, foundMeasurement.getPulse()),
                () -> assertEquals("Test measurement", foundMeasurement.getNote()),
                () -> assertEquals(new BigDecimal("180.00"), foundMeasurement.getHeightCm()),
                () -> assertEquals(new BigDecimal("85.50"), foundMeasurement.getWeightKg())
        );
    }

    @Test
    void findByPersonId_shouldReturnMeasurementsWithPagination() {

        // Test data.
        final Long personId = createTestPerson();
        final Long otherPersonId = createTestPerson();

        final LocalDateTime firstDate = LocalDateTime.of(2026, 10, 1, 9, 0);
        final LocalDateTime secondDate = LocalDateTime.of(2026, 10, 2, 9, 0);
        final LocalDateTime thirdDate = LocalDateTime.of(2026, 10, 3, 9, 0);

        createTestMeasurement(personId, firstDate);
        createTestMeasurement(personId, secondDate);
        createTestMeasurement(personId, thirdDate);
        createTestMeasurement(otherPersonId, thirdDate);

        entityManager.flush();
        entityManager.clear();

        // Execute.
        final Page<BloodPressureMeasurement> firstPage = measurementService.findByPersonId(
                personId,
                PageRequest.of(0, 2, Sort.by("measuredAt").descending())
        );

        final Page<BloodPressureMeasurement> secondPage = measurementService.findByPersonId(
                personId,
                PageRequest.of(1, 2, Sort.by("measuredAt").descending())
        );

        // Verify.
        assertAll(
                () -> assertEquals(3, firstPage.getTotalElements()),
                () -> assertEquals(2, firstPage.getTotalPages()),
                () -> assertEquals(2, firstPage.getContent().size()),
                () -> assertEquals(thirdDate, firstPage.getContent().getFirst().getMeasuredAt()),
                () -> assertEquals(secondDate, firstPage.getContent().get(1).getMeasuredAt()),
                () -> assertEquals(1, secondPage.getContent().size()),
                () -> assertEquals(firstDate, secondPage.getContent().getFirst().getMeasuredAt()),
                () -> assertTrue(firstPage.isFirst()),
                () -> assertTrue(secondPage.isLast())
        );
    }

    @Test
    void update_shouldPersistChanges() {

        // Test data.
        final Long personId = createTestPerson();
        final LocalDateTime originalDate = LocalDateTime.of(2026, 10, 1, 9, 0);
        final BloodPressureMeasurement createdMeasurement = createTestMeasurement(personId, originalDate);
        final Long measurementId = createdMeasurement.getId();

        final LocalDateTime updatedDate = LocalDateTime.of(2026, 10, 2, 18, 30);
        final Integer updatedSystolic = 135;
        final Integer updatedDiastolic = 85;
        final Integer updatedPulse = 78;
        final String updatedNote = "Updated measurement";
        final BigDecimal updatedHeightCm = new BigDecimal("181.00");
        final BigDecimal updatedWeightKg = new BigDecimal("84.50");

        final BloodPressureMeasurement measurement = new BloodPressureMeasurement();
        measurement.setPersonId(personId);
        measurement.setMeasuredAt(updatedDate);
        measurement.setSystolic(updatedSystolic);
        measurement.setDiastolic(updatedDiastolic);
        measurement.setPulse(updatedPulse);
        measurement.setNote(updatedNote);
        measurement.setHeightCm(updatedHeightCm);
        measurement.setWeightKg(updatedWeightKg);

        entityManager.flush();
        entityManager.clear();

        // Execute.
        final BloodPressureMeasurement updatedMeasurement =
                measurementService.update(measurementId, measurement);

        // Verify returned domain object.
        assertNotNull(updatedMeasurement);

        assertAll(
                () -> assertEquals(measurementId, updatedMeasurement.getId()),
                () -> assertEquals(personId, updatedMeasurement.getPersonId()),
                () -> assertEquals(updatedDate, updatedMeasurement.getMeasuredAt()),
                () -> assertEquals(updatedSystolic, updatedMeasurement.getSystolic()),
                () -> assertEquals(updatedDiastolic, updatedMeasurement.getDiastolic()),
                () -> assertEquals(updatedPulse, updatedMeasurement.getPulse()),
                () -> assertEquals(updatedNote, updatedMeasurement.getNote()),
                () -> assertEquals(updatedHeightCm, updatedMeasurement.getHeightCm()),
                () -> assertEquals(updatedWeightKg, updatedMeasurement.getWeightKg())
        );

        // Persist pending changes and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        // Verify persisted entity.
        final BloodPressureMeasurementEntity savedEntity =
                measurementRepository.findById(measurementId).orElseThrow();

        assertAll(
                () -> assertEquals(measurementId, savedEntity.getId()),
                () -> assertEquals(personId, savedEntity.getPerson().getId()),
                () -> assertEquals(updatedDate, savedEntity.getMeasuredAt()),
                () -> assertEquals(updatedSystolic, savedEntity.getSystolic()),
                () -> assertEquals(updatedDiastolic, savedEntity.getDiastolic()),
                () -> assertEquals(updatedPulse, savedEntity.getPulse()),
                () -> assertEquals(updatedNote, savedEntity.getNote()),
                () -> assertEquals(updatedHeightCm, savedEntity.getHeightCm()),
                () -> assertEquals(updatedWeightKg, savedEntity.getWeightKg())
        );
    }

    @Test
    void delete_shouldRemoveMeasurement() {

        // Test data.
        final Long personId = createTestPerson();
        final LocalDateTime measuredAt = LocalDateTime.of(2026, 10, 2, 12, 30);
        final BloodPressureMeasurement createdMeasurement = createTestMeasurement(personId, measuredAt);
        final Long measurementId = createdMeasurement.getId();

        entityManager.flush();
        entityManager.clear();

        // Verify measurement exists before deletion.
        assertTrue(measurementRepository.existsById(measurementId));

        // Execute.
        measurementService.delete(measurementId);

        // Persist pending changes and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        // Verify measurement was deleted.
        assertFalse(measurementRepository.existsById(measurementId));

        // Verify person was not deleted.
        assertTrue(personRepository.existsById(personId));
    }

    @Test
    void create_shouldRejectNonExistingPerson() {

        // Test data.
        final Long personId = -1L;

        final BloodPressureMeasurement measurement = new BloodPressureMeasurement();
        measurement.setPersonId(personId);
        measurement.setMeasuredAt(LocalDateTime.of(2026, 10, 2, 12, 30));
        measurement.setSystolic(125);
        measurement.setDiastolic(80);

        // Execute and verify.
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> measurementService.create(measurement)
        );

        assertEquals("Person not found: " + personId, exception.getMessage());
    }

    @Test
    void create_shouldRejectInvalidPressure() {

        // Test data.
        final Long personId = createTestPerson();

        final BloodPressureMeasurement measurement = new BloodPressureMeasurement();
        measurement.setPersonId(personId);
        measurement.setMeasuredAt(LocalDateTime.of(2026, 10, 2, 12, 30));
        measurement.setSystolic(80);
        measurement.setDiastolic(120);

        // Execute and verify.
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> measurementService.create(measurement)
        );

        assertEquals(
                "Systolic pressure must be greater than diastolic pressure",
                exception.getMessage()
        );
    }

    @Test
    void update_shouldRejectChangingPerson() {

        // Test data.
        final Long personId = createTestPerson();
        final Long otherPersonId = createTestPerson();

        final BloodPressureMeasurement createdMeasurement = createTestMeasurement(
                personId, LocalDateTime.of(2026, 10, 2, 12, 30));

        final BloodPressureMeasurement measurement = new BloodPressureMeasurement();
        measurement.setPersonId(otherPersonId);
        measurement.setMeasuredAt(createdMeasurement.getMeasuredAt());
        measurement.setSystolic(130);
        measurement.setDiastolic(85);

        entityManager.flush();
        entityManager.clear();

        // Execute and verify.
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> measurementService.update(createdMeasurement.getId(), measurement)
        );

        assertEquals(
                "Cannot change the person of an existing measurement",
                exception.getMessage()
        );
    }

}