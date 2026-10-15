package com.skripok.bloodpressure.person.web.dto;

import com.skripok.bloodpressure.person.domain.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PersonUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String middleName,
        @NotNull @Past LocalDate birthDate,
        @NotNull Gender gender,
        String phone,
        @NotNull Boolean active
) {
}