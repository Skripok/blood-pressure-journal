package com.skripok.bloodpressure.person.persistence.specification;

import com.skripok.bloodpressure.person.persistence.PersonEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import com.skripok.bloodpressure.person.domain.search.PersonSearchCriteria;

public final class PersonSpecification {

    private PersonSpecification() {
    }

    public static Specification<PersonEntity> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {

            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            final String[] nameParts = name
                    .trim()
                    .toLowerCase()
                    .split("\\s+");

            Predicate result = criteriaBuilder.conjunction();

            for (String namePart : nameParts) {
                final String pattern = "%" + namePart + "%";

                final Predicate namePartPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("firstName")),
                                pattern
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastName")),
                                pattern
                        )
                );

                result = criteriaBuilder.and(
                        result,
                        namePartPredicate
                );
            }

            return result;
        };
    }

    public static Specification<PersonEntity> middleNameContains(String middleName) {
        return (root, query, criteriaBuilder) -> {

            if (middleName == null || middleName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            final String pattern = "%" + middleName.trim().toLowerCase() + "%";

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("middleName")),
                    pattern
            );
        };
    }

    public static Specification<PersonEntity> phoneMatches(String phone) {
        return (root, query, criteriaBuilder) -> {

            if (phone == null || phone.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            final String digits = phone.replaceAll("\\D", "");

            final String searchablePhone = digits.length() > 9
                    ? digits.substring(digits.length() - 9)
                    : digits;

            return criteriaBuilder.like(
                    root.get("phone"),
                    "%" + searchablePhone
            );
        };
    }

    public static Specification<PersonEntity> birthDateEquals(LocalDate birthDate) {
        return (root, query, criteriaBuilder) -> {

            if (birthDate == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("birthDate"),
                    birthDate
            );
        };
    }

    public static Specification<PersonEntity> activeEquals(Boolean active) {
        return (root, query, criteriaBuilder) -> {

            if (active == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("active"),
                    active
            );
        };
    }

    public static Specification<PersonEntity> from(PersonSearchCriteria criteria) {
        if (criteria == null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.conjunction();
        }

        return Specification
                .where(nameContains(criteria.getName()))
                .and(middleNameContains(criteria.getMiddleName()))
                .and(phoneMatches(criteria.getPhone()))
                .and(birthDateEquals(criteria.getBirthDate()))
                .and(activeEquals(criteria.getActive()));
    }
}