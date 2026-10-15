package com.skripok.bloodpressure.person.web.mapper;

import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.web.dto.PersonCreateRequest;
import com.skripok.bloodpressure.person.web.dto.PersonListItemResponse;
import com.skripok.bloodpressure.person.web.dto.PersonResponse;
import com.skripok.bloodpressure.person.web.dto.PersonUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activeChangedAt", ignore = true)
    Person toDomain(PersonCreateRequest request);

    PersonResponse toResponse(Person person);

    PersonListItemResponse toListItemResponse(Person person);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "activeChangedAt", ignore = true)
    Person toDomain(Long id, PersonUpdateRequest request);
}