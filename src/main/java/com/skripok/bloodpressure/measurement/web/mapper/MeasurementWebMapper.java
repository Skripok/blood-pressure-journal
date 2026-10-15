package com.skripok.bloodpressure.measurement.web.mapper;

import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.web.dto.MeasurementRequest;
import com.skripok.bloodpressure.measurement.web.dto.MeasurementResponse;
import com.skripok.bloodpressure.measurement.web.dto.MeasurementListItemResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeasurementWebMapper {

    BloodPressureMeasurement toDomain(MeasurementRequest request);

    MeasurementResponse toResponse(BloodPressureMeasurement measurement);

    MeasurementListItemResponse toListItemResponse(BloodPressureMeasurement measurement);
}