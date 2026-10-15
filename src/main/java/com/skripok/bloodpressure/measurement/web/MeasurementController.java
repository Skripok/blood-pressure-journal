package com.skripok.bloodpressure.measurement.web;

import com.skripok.bloodpressure.common.web.dto.PageResponse;
import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.service.BloodPressureMeasurementService;
import com.skripok.bloodpressure.measurement.web.dto.MeasurementListItemResponse;
import com.skripok.bloodpressure.measurement.web.dto.MeasurementRequest;
import com.skripok.bloodpressure.measurement.web.dto.MeasurementResponse;
import com.skripok.bloodpressure.measurement.web.mapper.MeasurementWebMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final BloodPressureMeasurementService measurementService;
    private final MeasurementWebMapper measurementWebMapper;

    public MeasurementController(BloodPressureMeasurementService measurementService,
                                 MeasurementWebMapper measurementWebMapper) {
        this.measurementService = measurementService;
        this.measurementWebMapper = measurementWebMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeasurementResponse create(@RequestBody MeasurementRequest request) {
        final BloodPressureMeasurement measurement = measurementWebMapper.toDomain(request);
        return measurementWebMapper.toResponse(measurementService.create(measurement));
    }

    @GetMapping("/{id}")
    public MeasurementResponse findById(@PathVariable Long id) {
        return measurementWebMapper.toResponse(measurementService.findById(id));
    }

    @GetMapping
    public PageResponse<MeasurementListItemResponse> findByPersonId(
            @RequestParam Long personId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        final Pageable pageable = PageRequest.of(
                page, size, Sort.by("measuredAt").descending());

        return PageResponse.from(
                measurementService.findByPersonId(personId, pageable),
                measurementWebMapper::toListItemResponse);
    }

    @PutMapping("/{id}")
    public MeasurementResponse update(@PathVariable Long id, @RequestBody MeasurementRequest request) {
        final BloodPressureMeasurement measurement = measurementWebMapper.toDomain(request);
        return measurementWebMapper.toResponse(measurementService.update(id, measurement));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        measurementService.delete(id);
    }
}