package com.skripok.bloodpressure.measurement.mapper;

import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.persistence.BloodPressureMeasurementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BloodPressureMeasurementMapper {

    @Mapping(target = "personId", source = "person.id")
    BloodPressureMeasurement toDomain(BloodPressureMeasurementEntity entity);

    @Mapping(target = "person", ignore = true)
    BloodPressureMeasurementEntity toEntity(BloodPressureMeasurement measurement);
}