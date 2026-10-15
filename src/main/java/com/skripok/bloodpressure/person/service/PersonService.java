package com.skripok.bloodpressure.person.service;

import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.domain.search.PersonSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PersonService {

    Person create(Person person);

    Optional<Person> findById(Long id);

    Page<Person> findAll(Pageable pageable, boolean showAll);

    Person update(Person person);

    Page<Person> search(
            PersonSearchCriteria criteria,
            Pageable pageable
    );
}