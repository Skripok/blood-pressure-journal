package com.skripok.bloodpressure.person.web.dto;

import com.skripok.bloodpressure.person.domain.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PersonResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        LocalDate birthDate,
        Gender gender,
        String phone,
        boolean active,
        LocalDateTime activeChangedAt
) {
}