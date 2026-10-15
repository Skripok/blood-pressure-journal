package com.skripok.bloodpressure.measurement.mapper;

import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.web.form.MeasurementForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeasurementFormMapper {

    @Mapping(target = "id", ignore = true)
    BloodPressureMeasurement toDomain(MeasurementForm form);

    MeasurementForm toForm(BloodPressureMeasurement measurement);
}