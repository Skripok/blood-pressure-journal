package com.skripok.bloodpressure.person.domain;

import com.skripok.bloodpressure.person.domain.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Person {

    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    private Gender gender;

    private boolean active;

    private LocalDateTime activeChangedAt;

    private String phone;

    public Person(Long id, String firstName, String lastName, String middleName, LocalDate birthDate, Gender gender,
    boolean active, LocalDateTime activeChangedAt, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.active = active;
        this.activeChangedAt = activeChangedAt;
        this.phone =phone;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getActiveChangedAt() {
        return activeChangedAt;
    }

    public void setActiveChangedAt(LocalDateTime activeChangedAt) {
        this.activeChangedAt = activeChangedAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}