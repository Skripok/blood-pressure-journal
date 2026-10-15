package com.skripok.bloodpressure.person.web;

import com.skripok.bloodpressure.common.web.dto.PageResponse;
import com.skripok.bloodpressure.person.domain.Person;
import com.skripok.bloodpressure.person.service.PersonService;
import com.skripok.bloodpressure.person.web.dto.PersonCreateRequest;
import com.skripok.bloodpressure.person.web.dto.PersonListItemResponse;
import com.skripok.bloodpressure.person.web.dto.PersonResponse;
import com.skripok.bloodpressure.person.web.enums.PhoneCountry;
import com.skripok.bloodpressure.person.web.mapper.PersonWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.skripok.bloodpressure.person.web.dto.PersonUpdateRequest;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;
    private final PersonWebMapper personWebMapper;

    public PersonController(PersonService personService, PersonWebMapper personWebMapper) {
        this.personService = personService;
        this.personWebMapper = personWebMapper;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse create(@Valid @RequestBody PersonCreateRequest request) {
        final Person person = personWebMapper.toDomain(request);
        final Person createdPerson = personService.create(person);

        return personWebMapper.toResponse(createdPerson);
    }

    @GetMapping("/{id}")
    public PersonResponse findById(@PathVariable Long id) {
        final Person person = personService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person not found: " + id));

        return personWebMapper.toResponse(person);
    }

    @GetMapping
    public PageResponse<PersonListItemResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean showAll) {

        final Pageable pageable = PageRequest.of(page, size,
                Sort.by("lastName", "firstName", "id").ascending());

        return PageResponse.from(
                personService.findAll(pageable, showAll),
                personWebMapper::toListItemResponse
        );
    }

    @PutMapping("/{id}")
    public PersonResponse update(@PathVariable Long id, @Valid @RequestBody PersonUpdateRequest request) {
        final Person person = personWebMapper.toDomain(id, request);
        final Person updatedPerson = personService.update(person);

        return personWebMapper.toResponse(updatedPerson);
    }
}