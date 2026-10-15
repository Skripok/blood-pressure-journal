package com.skripok.bloodpressure.person.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNormalizerTest {

    @ParameterizedTest
    @CsvSource({
            "'+380 (97) 977-97-97', 380979779797",
            "'+48 501 234 567', 48501234567",
            "'+420 777 123 456', 420777123456",
            "380979779797, 380979779797",
            "'097 977 97 97', 0979779797"
    })
    void normalize_shouldRemoveFormatting(
            final String input,
            final String expected
    ) {
        final String result = PhoneNormalizer.normalize(input);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void normalize_shouldReturnNullForMissingPhone(final String input) {
        final String result = PhoneNormalizer.normalize(input);

        assertNull(result);
    }

    @Test
    void normalize_shouldRejectInvalidPhone() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PhoneNormalizer.normalize("+380 ABC 123")
        );
    }
}