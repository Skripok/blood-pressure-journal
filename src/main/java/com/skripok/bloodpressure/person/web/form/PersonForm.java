package com.skripok.bloodpressure.person.web.form;

import com.skripok.bloodpressure.person.domain.enums.Gender;
import com.skripok.bloodpressure.person.web.enums.PhoneCountry;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PersonForm {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    
    private Gender gender;

    private PhoneCountry phoneCountry;
    private String phoneNumber;

    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PhoneCountry getPhoneCountry() {
        return phoneCountry;
    }

    public void setPhoneCountry(PhoneCountry phoneCountry) {
        this.phoneCountry = phoneCountry;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}