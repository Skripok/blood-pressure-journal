package com.skripok.bloodpressure.person.web.dto;

import java.time.LocalDate;

public record PersonListItemResponse(Long id, String firstName, String lastName,
                                     String middleName, LocalDate birthDate, boolean active) {
}