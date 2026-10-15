package com.skripok.bloodpressure.person.service;

import com.skripok.bloodpressure.common.exception.ResourceNotFoundException;
import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.domain.search.PersonSearchCriteria;
import com.skripok.bloodpressure.person.mapper.PersonMapper;
import com.skripok.bloodpressure.person.persistence.PersonEntity;
import com.skripok.bloodpressure.person.persistence.PersonRepository;

import com.skripok.bloodpressure.person.persistence.specification.PersonSpecification;
import com.skripok.bloodpressure.person.util.PhoneNormalizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import java.time.temporal.ChronoUnit;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final Clock clock;

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper, Clock clock) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Person create(Person person) {
        if (person == null) {
            throw new IllegalArgumentException(
                    "Person must not be null"
            );
        }

        person.setPhone(PhoneNormalizer.normalize(person.getPhone()));
        PersonEntity entity = personMapper.toNewEntity(person);
        entity.setActiveChangedAt(LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS));

        PersonEntity savedEntity = personRepository.save(entity);

        return personMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Person> findAll(Pageable pageable, boolean showAll) {

        if (pageable == null) {

            // TODO: Load the default page size from user preferences.
            pageable = PageRequest.of(
                    0,
                    20,
                    Sort.by("lastName", "firstName", "id").ascending()
            );
        }

        Page<PersonEntity> page = showAll
                ? personRepository.findAll(pageable)
                : personRepository.findByActive(true, pageable);

        return page.map(personMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id)
                .map(personMapper::toDomain);
    }

    @Override
    @Transactional
    public Person update(Person person) {

        if (person == null || person.getId() == null) {
            throw new IllegalArgumentException(
                    "Person and person ID must not be null"
            );
        }

        person.setPhone(PhoneNormalizer.normalize(person.getPhone()));
        PersonEntity entity = personRepository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found: " + person.getId()
                ));

        boolean activeChanged = entity.isActive() != person.isActive();
        personMapper.updateEntity(person, entity);

        if (activeChanged) {
            // TODO: Consider moving activeChangedAt management to a database trigger (if it will be possible)
            // to track status changes made directly through SQL.
            entity.setActiveChangedAt(LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS));
        }

        return personMapper.toDomain(entity);
    }

    @Override
    public Page<Person> search(PersonSearchCriteria criteria, Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, 20);
        }

        return personRepository
                .findAll(PersonSpecification.from(criteria), pageable)
                .map(personMapper::toDomain);
    }
}