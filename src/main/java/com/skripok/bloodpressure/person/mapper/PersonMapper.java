package com.skripok.bloodpressure.person.mapper;

import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.persistence.PersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    Person toDomain(PersonEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activeChangedAt", ignore = true)
    PersonEntity toNewEntity(Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activeChangedAt", ignore = true)
    void updateEntity(Person person, @MappingTarget PersonEntity entity);
}