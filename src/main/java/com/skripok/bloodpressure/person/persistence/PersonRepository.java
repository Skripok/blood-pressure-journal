package com.skripok.bloodpressure.person.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonRepository
        extends JpaRepository<PersonEntity, Long>, JpaSpecificationExecutor<PersonEntity> {
    Page<PersonEntity> findByActive(boolean active, Pageable pageable);
}