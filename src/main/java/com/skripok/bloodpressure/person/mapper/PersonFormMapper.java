    package com.skripok.bloodpressure.person.mapper;

import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.web.enums.PhoneCountry;
import com.skripok.bloodpressure.person.web.form.PersonForm;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Arrays;

@Mapper(componentModel = "spring")
public interface PersonFormMapper {

    @Mapping(target = "phoneCountry", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    PersonForm toForm(Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activeChangedAt", ignore = true)
    @Mapping(target = "phone", expression = "java(buildPhone(form))")
    Person toDomain(PersonForm form);

    @AfterMapping
    default void mapPhone(Person person, @MappingTarget PersonForm form) {
        if (person.getPhone() == null || person.getPhone().isBlank()) {
            return;
        }

        final String phone = person.getPhone();

        Arrays.stream(PhoneCountry.values())
                .filter(country -> phone.startsWith(country.getCode()))
                .findFirst()
                .ifPresent(country -> {
                    form.setPhoneCountry(country);
                    form.setPhoneNumber(phone.substring(country.getCode().length()));
                });
    }

    default String buildPhone(PersonForm form) {
        if (form.getPhoneCountry() == null
                || form.getPhoneNumber() == null
                || form.getPhoneNumber().isBlank()) {
            return null;
        }

        final String phoneNumber = form.getPhoneNumber().replaceAll("\\D", "");

        if (phoneNumber.isBlank()) {
            return null;
        }

        return form.getPhoneCountry().getCode() + phoneNumber;
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "activeChangedAt", ignore = true)
    @Mapping(target = "phone", expression = "java(buildPhone(form))")
    Person toDomain(Long id, PersonForm form);
}