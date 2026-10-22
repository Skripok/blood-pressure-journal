package com.skripok.bloodpressure.measurement.web;

import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.domain.LatestBodyMeasurements;
import com.skripok.bloodpressure.measurement.mapper.MeasurementFormMapper;
import com.skripok.bloodpressure.measurement.service.BloodPressureMeasurementService;
import com.skripok.bloodpressure.measurement.web.form.MeasurementForm;
import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Controller
public class MeasurementPageController {

    private final BloodPressureMeasurementService measurementService;
    private final PersonService personService;
    private final MeasurementFormMapper measurementFormMapper;

    public MeasurementPageController(
            BloodPressureMeasurementService measurementService,
            PersonService personService, MeasurementFormMapper measurementFormMapper) {

        this.measurementService = measurementService;
        this.personService = personService;
        this.measurementFormMapper = measurementFormMapper;
    }

    @GetMapping("/measurements/new")
    public String newMeasurement(@RequestParam Long personId, Model model) {
        final Person person = personService.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Person not found: " + personId
                ));

        final LatestBodyMeasurements latestBodyMeasurements =
                measurementService.findLatestBodyMeasurements(personId);

        final MeasurementForm measurementForm = new MeasurementForm();
        measurementForm.setPersonId(personId);
        measurementForm.setHeightCm(latestBodyMeasurements.heightCm());
        measurementForm.setWeightKg(latestBodyMeasurements.weightKg());

        model.addAttribute("person", person);
        model.addAttribute("measurementForm", measurementForm);
        model.addAttribute("formMode", "CREATE");

        return "measurement-form";
    }

    @PostMapping("/measurements")
    public String createMeasurement(@ModelAttribute MeasurementForm measurementForm) {
        final BloodPressureMeasurement measurement =
                measurementFormMapper.toDomain(measurementForm);

        measurementService.create(measurement);

        return "redirect:/measurements?personId=" + measurementForm.getPersonId();
    }

    @GetMapping("/measurements")
    public String measurements(
            @RequestParam Long personId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        final Person person = personService.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Person not found: " + personId
                ));

        final  Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("measuredAt"),
                        Sort.Order.desc("id")
                )

        );

        final Page<BloodPressureMeasurement> measurements =
                measurementService.findByPersonId(personId, pageable);

        model.addAttribute("person", person);
        model.addAttribute("measurements", measurements);

        return "measurements";
    }

    @GetMapping("/measurements/{id}/edit")
    public String editMeasurement(@PathVariable Long id, Model model) {
        final BloodPressureMeasurement measurement =
                measurementService.findById(id);

        final Person person = personService.findById(measurement.getPersonId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Person not found: " + measurement.getPersonId()
                ));

        model.addAttribute("person", person);
        model.addAttribute(
                "measurementForm",
                measurementFormMapper.toForm(measurement)
        );
        model.addAttribute("measurementId", id);
        model.addAttribute("formMode", "EDIT");

        return "measurement-form";
    }

    @PostMapping("/measurements/{id}")
    public String updateMeasurement(
            @PathVariable Long id,
            @ModelAttribute MeasurementForm measurementForm) {

        final BloodPressureMeasurement measurement =
                measurementFormMapper.toDomain(measurementForm);

        measurementService.update(id, measurement);

        return "redirect:/measurements?personId=" + measurementForm.getPersonId();
    }
}