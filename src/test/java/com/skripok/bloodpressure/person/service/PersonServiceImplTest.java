package com.skripok.bloodpressure.person.service;

import com.skripok.bloodpressure.person.domain.search.PersonSearchCriteria;
import com.skripok.bloodpressure.support.MutableClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.domain.enums.Gender;
import com.skripok.bloodpressure.person.persistence.PersonEntity;
import com.skripok.bloodpressure.person.persistence.PersonRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Instant;
import java.time.ZoneId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
@Transactional
@Import(PersonServiceImplTest.TestClockConfig.class)
public class PersonServiceImplTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntityManager entityManager;

    private static final ZoneId TEST_ZONE = ZoneId.of("Europe/Kyiv");

    private static final Instant INITIAL_INSTANT =
            Instant.parse("2026-01-15T10:30:00Z");

    private static final Instant UPDATED_INSTANT =
            Instant.parse("2026-01-16T12:45:00Z");

    @Autowired
    private MutableClock clock;

    @BeforeEach
    void resetClock() {
        clock.setInstant(INITIAL_INSTANT);
    }


    private Person createTestPerson(final boolean active) {
        return new Person(
                null,
                "John",
                "Smith",
                "Michael",
                LocalDate.of(1985, 5, 15),
                Gender.MALE,
                active,
                null,
                "+380 (97) 977-97-97"
        );
    }

    @TestConfiguration
    static class TestClockConfig {

        @Bean
        @Primary
        MutableClock testClock() {
            return new MutableClock(INITIAL_INSTANT, TEST_ZONE);
        }
    }

    @Test
    void create_shouldCreateActivePerson() {

        // Test data.
        final String firstName = "John";
        final String lastName = "Smith";
        final String middleName = "Michael";
        final LocalDate birthDate = LocalDate.of(1985, 5, 15);
        final Gender gender = Gender.MALE;
        final boolean active = true;
        final String phone = "+380 (97) 977-97-97";
        final String normalizedPhone = "380979779797";

        final Person person = new Person(
                null,
                firstName,
                lastName,
                middleName,
                birthDate,
                gender,
                active,
                null,
                phone
        );

        // Execute.
        final Person createdPerson = personService.create(person);

        // Verify returned domain object.
        assertNotNull(createdPerson);
        assertNotNull(createdPerson.getId());

        assertAll(
                () -> assertEquals(firstName, createdPerson.getFirstName()),
                () -> assertEquals(lastName, createdPerson.getLastName()),
                () -> assertEquals(middleName, createdPerson.getMiddleName()),
                () -> assertEquals(birthDate, createdPerson.getBirthDate()),
                () -> assertEquals(gender, createdPerson.getGender()),
                () -> assertEquals(active, createdPerson.isActive()),
                () -> assertEquals(normalizedPhone, createdPerson.getPhone())
        );

        final LocalDateTime expectedActiveChangedAt =
                LocalDateTime.ofInstant(INITIAL_INSTANT, TEST_ZONE);

        // Verify status change timestamp.
        assertEquals(expectedActiveChangedAt, createdPerson.getActiveChangedAt());

        // Persist pending changes and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        // Verify persisted entity.
        final PersonEntity savedEntity = personRepository
                .findById(createdPerson.getId())
                .orElseThrow();

        assertAll(
                () -> assertEquals(createdPerson.getId(), savedEntity.getId()),
                () -> assertEquals(firstName, savedEntity.getFirstName()),
                () -> assertEquals(lastName, savedEntity.getLastName()),
                () -> assertEquals(middleName, savedEntity.getMiddleName()),
                () -> assertEquals(birthDate, savedEntity.getBirthDate()),
                () -> assertEquals(gender, savedEntity.getGender()),
                () -> assertEquals(active, savedEntity.isActive()),
                () -> assertEquals(
                        createdPerson.getActiveChangedAt(),
                        savedEntity.getActiveChangedAt()
                ),
                () -> assertEquals(normalizedPhone, savedEntity.getPhone())
        );
    }

    @Test
    void create_shouldCreateInactivePerson() {

        // Test data.
        final boolean active = false;
        final Person person = createTestPerson(active);

        // Execute.
        final Person createdPerson = personService.create(person);

        // Verify returned domain object.
        assertNotNull(createdPerson);
        assertNotNull(createdPerson.getId());

        assertFalse(createdPerson.isActive());

        final LocalDateTime expectedActiveChangedAt = LocalDateTime.ofInstant(INITIAL_INSTANT, TEST_ZONE);

        assertEquals(expectedActiveChangedAt, createdPerson.getActiveChangedAt());

        // Verify persisted data.
        entityManager.flush();
        entityManager.clear();

        final PersonEntity savedEntity = personRepository
                .findById(createdPerson.getId())
                .orElseThrow();

        assertAll(
                () -> assertFalse(savedEntity.isActive()),
                () -> assertEquals(
                        createdPerson.getActiveChangedAt(),
                        savedEntity.getActiveChangedAt()
                )
        );
    }

    @Test
    void update_shouldChangeActiveStatusAndTimestamp() {

        // Test data.
        final boolean initialActive = true;
        final boolean updatedActive = false;

        final Person person = createTestPerson(initialActive);
        final Person createdPerson = personService.create(person);

        final Long personId = createdPerson.getId();

        final LocalDateTime originalActiveChangedAt = createdPerson.getActiveChangedAt();

        final LocalDateTime expectedActiveChangedAt = LocalDateTime.ofInstant(UPDATED_INSTANT, TEST_ZONE);

        // Change the clock to simulate a later update.
        clock.setInstant(UPDATED_INSTANT);

        // Execute.
        createdPerson.setActive(updatedActive);

        final Person updatedPerson = personService.update(createdPerson);

        // Verify returned domain object.
        assertAll(
                () -> assertEquals(personId, updatedPerson.getId()),
                () -> assertEquals(updatedActive, updatedPerson.isActive()),
                () -> assertNotEquals(
                        originalActiveChangedAt,
                        updatedPerson.getActiveChangedAt()
                ),
                () -> assertEquals(
                        expectedActiveChangedAt,
                        updatedPerson.getActiveChangedAt()
                )
        );

        // Persist pending changes and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        // Verify persisted data.
        final PersonEntity savedEntity = personRepository
                .findById(personId)
                .orElseThrow();

        assertAll(
                () -> assertEquals(updatedActive, savedEntity.isActive()),
                () -> assertEquals(
                        expectedActiveChangedAt,
                        savedEntity.getActiveChangedAt()
                )
        );
    }

    @Test
    void update_shouldPreserveTimestampWhenActiveIsUnchanged() {

        // Test data.
        final boolean active = true;
        final String updatedLastName = "Johnson";

        final Person person = createTestPerson(active);
        final Person createdPerson = personService.create(person);

        final Long personId = createdPerson.getId();
        final LocalDateTime originalActiveChangedAt = createdPerson.getActiveChangedAt();

        // Simulate an update at a later time.
        clock.setInstant(UPDATED_INSTANT);

        // Change personal data without changing the active status.
        createdPerson.setLastName(updatedLastName);

        // Execute.
        final Person updatedPerson = personService.update(createdPerson);

        // Verify returned domain object.
        assertAll(
                () -> assertEquals(personId, updatedPerson.getId()),
                () -> assertEquals(updatedLastName, updatedPerson.getLastName()),
                () -> assertEquals(active, updatedPerson.isActive()),
                () -> assertEquals(originalActiveChangedAt, updatedPerson.getActiveChangedAt()
                )
        );

        // Persist pending changes and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        // Verify persisted data.
        final PersonEntity savedEntity = personRepository
                .findById(personId)
                .orElseThrow();

        assertAll(
                () -> assertEquals(updatedLastName, savedEntity.getLastName()),
                () -> assertEquals(active, savedEntity.isActive()),
                () -> assertEquals(
                        originalActiveChangedAt,
                        savedEntity.getActiveChangedAt()
                )
        );
    }

    @Test
    void findAll_shouldFilterInactivePersonsWhenShowAllIsFalse() {

        // Test data.
        final Person activePerson1 = personService.create(createTestPerson(true));
        final Person activePerson2 = personService.create(createTestPerson(true));
        final Person inactivePerson = personService.create(createTestPerson(false));

        final Pageable pageable = PageRequest.of(0, 20);

        // Persist test data and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        // Execute.
        final Page<Person> activePage = personService.findAll(pageable, false);
        final Page<Person> allPage = personService.findAll(pageable, true);

        // Verify active-only search.
        assertAll(
                () -> assertEquals(2, activePage.getTotalElements()),
                () -> assertEquals(2, activePage.getContent().size()),
                () -> assertTrue(activePage.getContent().stream()
                        .allMatch(Person::isActive)),
                () -> assertTrue(activePage.getContent().stream()
                        .anyMatch(p -> p.getId().equals(activePerson1.getId()))),
                () -> assertTrue(activePage.getContent().stream()
                        .anyMatch(p -> p.getId().equals(activePerson2.getId())))
        );

        // Verify search including inactive persons.
        assertAll(
                () -> assertEquals(3, allPage.getTotalElements()),
                () -> assertEquals(3, allPage.getContent().size()),
                () -> assertTrue(allPage.getContent().stream()
                        .anyMatch(p -> p.getId().equals(inactivePerson.getId()))),
                () -> assertEquals(1, allPage.getContent().stream()
                        .filter(p -> !p.isActive())
                        .count())
        );
    }

    @Test
    void update_shouldUpdateAllFieldsAndClearNullableFields() {

        // Prepare test data.
        final Person person = createTestPerson(true);
        final Person createdPerson = personService.create(person);
        final Long personId = createdPerson.getId();

        // First update: change all editable fields.
        final String updatedFirstName = "Jane";
        final String updatedLastName = "Johnson";
        final String updatedMiddleName = "Elizabeth";
        final LocalDate updatedBirthDate = LocalDate.of(1990, 3, 12);
        final Gender updatedGender = Gender.FEMALE;
        final String updatedPhone = "+48 501 234 567";
        final String normalizedPhone = "48501234567";

        createdPerson.setFirstName(updatedFirstName);
        createdPerson.setLastName(updatedLastName);
        createdPerson.setMiddleName(updatedMiddleName);
        createdPerson.setBirthDate(updatedBirthDate);
        createdPerson.setGender(updatedGender);
        createdPerson.setPhone(updatedPhone);

        // Execute first update.
        final Person updatedPerson = personService.update(createdPerson);

        // Verify returned domain object.
        assertAll(
                () -> assertEquals(personId, updatedPerson.getId()),
                () -> assertEquals(updatedFirstName, updatedPerson.getFirstName()),
                () -> assertEquals(updatedLastName, updatedPerson.getLastName()),
                () -> assertEquals(updatedMiddleName, updatedPerson.getMiddleName()),
                () -> assertEquals(updatedBirthDate, updatedPerson.getBirthDate()),
                () -> assertEquals(updatedGender, updatedPerson.getGender()),
                () -> assertEquals(normalizedPhone, updatedPerson.getPhone())
        );

        entityManager.flush();
        entityManager.clear();

        // Verify persisted data after first update.
        final PersonEntity savedEntity = personRepository.findById(personId)
                .orElseThrow();

        assertAll(
                () -> assertEquals(updatedFirstName, savedEntity.getFirstName()),
                () -> assertEquals(updatedLastName, savedEntity.getLastName()),
                () -> assertEquals(updatedMiddleName, savedEntity.getMiddleName()),
                () -> assertEquals(updatedBirthDate, savedEntity.getBirthDate()),
                () -> assertEquals(updatedGender, savedEntity.getGender()),
                () -> assertEquals(normalizedPhone, savedEntity.getPhone())
        );

        // Second update: clear all nullable editable fields.
        updatedPerson.setMiddleName(null);
        updatedPerson.setPhone(null);

        // Execute second update.
        final Person clearedPerson = personService.update(updatedPerson);

        // Verify returned domain object.
        assertAll(
                () -> assertNull(clearedPerson.getMiddleName()),
                () -> assertNull(clearedPerson.getPhone())
        );

        entityManager.flush();
        entityManager.clear();

        // Verify persisted data after clearing nullable fields.
        final PersonEntity clearedEntity = personRepository.findById(personId)
                .orElseThrow();

        assertAll(
                () -> assertNull(clearedEntity.getMiddleName()),
                () -> assertNull(clearedEntity.getPhone())
        );
    }

    @Test
    void search_shouldFindPersonByCombinedCriteria() {
        final Person person = createTestPerson(true);
        final Person createdPerson = personService.create(person);

        entityManager.flush();
        entityManager.clear();

        final PersonSearchCriteria criteria = new PersonSearchCriteria();
        criteria.setName("John Smith");
        criteria.setMiddleName("Michael");
        criteria.setPhone("+380 (97) 977-97-97");
        criteria.setBirthDate(LocalDate.of(1985, 5, 15));
        criteria.setActive(true);

        final Page<Person> result = personService.search(criteria, PageRequest.of(0, 20));

        assertAll(
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(createdPerson.getId(), result.getContent().getFirst().getId())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "John",
            "Smith",
            "John Smith",
            "Smith John",
            "john smith",
            "SMITH JOHN"
    })
    void search_shouldFindPersonByName(String name) {
        final Person createdPerson = personService.create(createTestPerson(true));

        entityManager.flush();
        entityManager.clear();

        final PersonSearchCriteria criteria = new PersonSearchCriteria();
        criteria.setName(name);

        final Page<Person> result = personService.search(criteria, PageRequest.of(0, 20));

        assertAll(
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(createdPerson.getId(), result.getContent().getFirst().getId())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+380 (97) 977-97-97",
            "380979779797",
            "0979779797",
            "979779797"
    })
    void search_shouldFindPersonByPhone(String phone) {
        final Person createdPerson = personService.create(createTestPerson(true));

        entityManager.flush();
        entityManager.clear();

        final PersonSearchCriteria criteria = new PersonSearchCriteria();
        criteria.setPhone(phone);

        final Page<Person> result = personService.search(criteria, PageRequest.of(0, 20));

        assertAll(
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(createdPerson.getId(), result.getContent().getFirst().getId())
        );
    }

    @Test
    void search_shouldNotFindPersonByDifferentPhone() {
        personService.create(createTestPerson(true));

        entityManager.flush();
        entityManager.clear();

        final PersonSearchCriteria criteria = new PersonSearchCriteria();
        criteria.setPhone("0971111111");

        final Page<Person> result = personService.search(criteria, PageRequest.of(0, 20));

        assertTrue(result.isEmpty());
    }
}
