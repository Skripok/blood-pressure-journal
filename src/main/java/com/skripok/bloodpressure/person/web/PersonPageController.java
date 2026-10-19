package com.skripok.bloodpressure.person.web;

import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.domain.search.PersonSearchCriteria;
import com.skripok.bloodpressure.person.mapper.PersonFormMapper;
import com.skripok.bloodpressure.person.service.PersonService;
import com.skripok.bloodpressure.person.web.enums.PersonPageMode;
import com.skripok.bloodpressure.person.web.enums.PhoneCountry;
import com.skripok.bloodpressure.person.web.form.PersonForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class PersonPageController {

    private final PersonService personService;
    private final PersonFormMapper personFormMapper;

    public PersonPageController(PersonService personService, PersonFormMapper personFormMapper) {
        this.personService = personService;
        this.personFormMapper = personFormMapper;
    }

    @ModelAttribute("phoneCountries")
    public PhoneCountry[] phoneCountries() {
        return PhoneCountry.values();
    }

    @GetMapping("/persons/select")
    public String selectPerson(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(defaultValue = "active") String personStatus,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        return showPersons(
                name,
                middleName,
                phone,
                birthDate,
                personStatus,
                PersonPageMode.SELECT,
                page,
                model
        );
    }

    @GetMapping("/settings/persons")
    public String managePersons(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(defaultValue = "active") String personStatus,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        return showPersons(
                name,
                middleName,
                phone,
                birthDate,
                personStatus,
                PersonPageMode.MANAGE,
                page,
                model
        );
    }

    @GetMapping("/persons/charts")
    public String selectPersonForCharts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(defaultValue = "active") String personStatus,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        return showPersons(
                name,
                middleName,
                phone,
                birthDate,
                personStatus,
                PersonPageMode.CHARTS,
                page,
                model
        );
    }

    private String showPersons(
            String name,
            String middleName,
            String phone,
            LocalDate birthDate,
            String personStatus,
            PersonPageMode pageMode,
            int page,
            Model model) {

        final PersonSearchCriteria criteria = new PersonSearchCriteria();
        criteria.setName(name);
        criteria.setMiddleName(middleName);
        criteria.setPhone(phone);
        criteria.setBirthDate(birthDate);
        criteria.setActive(parseActiveStatus(personStatus));

        final Page<Person> persons = personService.search(
                criteria,
                PageRequest.of(
                        Math.max(0, page),
                        20,
                        Sort.by(
                                Sort.Order.asc("lastName"),
                                Sort.Order.asc("firstName"),
                                Sort.Order.asc("id")
                        )
                )
        );

        model.addAttribute("persons", persons.getContent());
        model.addAttribute("personsPage", persons);
        model.addAttribute("name", name);
        model.addAttribute("middleName", middleName);
        model.addAttribute("phone", phone);
        model.addAttribute("birthDate", birthDate);
        model.addAttribute("personStatus", personStatus);
        model.addAttribute("pageMode", pageMode);

        final String personsUrl = switch (pageMode) {
            case MANAGE -> "/settings/persons";
            case SELECT -> "/persons/select";
            case HISTORY -> "/persons/history";
            case CHARTS -> "/persons/charts";
        };

        model.addAttribute("personsUrl", personsUrl);

        return "persons";
    }

    private Boolean parseActiveStatus(String personStatus) {
        return switch (personStatus) {
            case "inactive" -> false;
            case "all" -> null;
            default -> true;
        };
    }

    @GetMapping("/settings/persons/new")
    public String newPerson(Model model) {
        final PersonForm personForm = new PersonForm();
        personForm.setPhoneCountry(PhoneCountry.UKRAINE);

        model.addAttribute("personForm", personForm);
        model.addAttribute("formMode", "CREATE");

        return "person-form";
    }

    @GetMapping("/settings/persons/{id}/edit")
    public String editPerson(@PathVariable Long id, Model model) {
        final Person person = personService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Person not found: " + id
                ));

        model.addAttribute("personForm", personFormMapper.toForm(person));
        model.addAttribute("formMode", "EDIT");

        return "person-form";
    }

    @PostMapping("/settings/persons")
    public String createPerson(@ModelAttribute PersonForm personForm) {
        final Person person = personFormMapper.toDomain(personForm);
        personService.create(person);

        return "redirect:/settings/persons";
    }

    @PostMapping("/settings/persons/{id}")
    public String updatePerson(
            @PathVariable Long id,
            @ModelAttribute PersonForm personForm) {

        final Person person = personFormMapper.toDomain(id, personForm);
        personService.update(person);

        return "redirect:/settings/persons";
    }

    @GetMapping("/persons/history")
    public String measurementHistory(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(defaultValue = "active") String personStatus,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        return showPersons(
                name,
                middleName,
                phone,
                birthDate,
                personStatus,
                PersonPageMode.HISTORY,
                page,
                model
        );
    }

}