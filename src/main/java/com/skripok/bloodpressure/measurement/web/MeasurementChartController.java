
package com.skripok.bloodpressure.measurement.web;

import com.skripok.bloodpressure.measurement.domain.BloodPressureMeasurement;
import com.skripok.bloodpressure.measurement.service.BloodPressureMeasurementService;
import com.skripok.bloodpressure.measurement.web.dto.ChartMeasurementDto;
import com.skripok.bloodpressure.person.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Controller
public class MeasurementChartController {

    private static final Map<String, String> TIME_OF_DAY_LABELS = Map.of(
            "all", "Весь день",
            "morning", "Ранок",
            "day", "День",
            "evening", "Вечір",
            "night", "Ніч"
    );

    private final BloodPressureMeasurementService measurementService;
    private final PersonService personService;
    private final JsonMapper jsonMapper;

    public MeasurementChartController(
            BloodPressureMeasurementService measurementService,
            PersonService personService,
            JsonMapper jsonMapper) {

        this.measurementService = measurementService;
        this.personService = personService;
        this.jsonMapper = jsonMapper;
    }

    @GetMapping("/measurements/charts")
    public String showCharts(
            @RequestParam Long personId,
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "all") String timeOfDay,
            Model model) {

        // Validate the selected period.
        if (days < 1 || days > 365) {
            throw new IllegalArgumentException("Invalid chart period");
        }

        // Validate the time-of-day filter.
        if (!TIME_OF_DAY_LABELS.containsKey(timeOfDay)) {
            throw new IllegalArgumentException(
                    "Invalid time of day: " + timeOfDay
            );
        }

        final LocalDate today = LocalDate.now();
        final LocalDateTime from = today.minusDays(days - 1L).atStartOfDay();
        final LocalDateTime to = today.plusDays(1).atStartOfDay();

        final var person = personService.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Person not found: " + personId
                ));

        // Load measurements for the selected date range.
        final List<BloodPressureMeasurement> periodMeasurements =
                measurementService.findByPersonIdAndPeriod(
                        personId,
                        from,
                        to
                );

        // Apply the time-of-day filter.
        final List<BloodPressureMeasurement> measurements =
                periodMeasurements.stream()
                        .filter(m -> matchesTimeOfDay(
                                m.getMeasuredAt().toLocalTime(),
                                timeOfDay
                        ))
                        .toList();

        // Prepare data for Chart.js.
        final var chartData = measurements.stream()
                .map(m -> new ChartMeasurementDto(
                        m.getMeasuredAt(),
                        m.getSystolic(),
                        m.getDiastolic(),
                        m.getPulse()
                ))
                .toList();

        model.addAttribute(
                "chartDataJson",
                jsonMapper.writeValueAsString(chartData)
        );

        model.addAttribute("person", person);
        model.addAttribute("measurements", measurements);
        model.addAttribute("days", days);
        model.addAttribute("fromDate", from.toLocalDate());
        model.addAttribute("toDate", today);

        model.addAttribute("timeOfDay", timeOfDay);
        model.addAttribute(
                "timeOfDayLabel",
                TIME_OF_DAY_LABELS.get(timeOfDay)
        );

        return "measurement-charts";
    }

    private boolean matchesTimeOfDay(LocalTime time, String timeOfDay) {
        return switch (timeOfDay) {
            case "all" -> true;

            case "morning" ->
                    !time.isBefore(LocalTime.of(6, 0))
                            && time.isBefore(LocalTime.of(12, 0));

            case "day" ->
                    !time.isBefore(LocalTime.of(12, 0))
                            && time.isBefore(LocalTime.of(17, 0));

            case "evening" ->
                    !time.isBefore(LocalTime.of(17, 0));

            case "night" ->
                    time.isBefore(LocalTime.of(6, 0));

            default -> false;
        };
    }
}
