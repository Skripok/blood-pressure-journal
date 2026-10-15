package com.skripok.bloodpressure.person.util;

public final class PhoneNormalizer {

    private PhoneNormalizer() {
    }

    public static String normalize(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        // Remove phone formatting characters.
        final String digits = phone.replaceAll("[\\s()+-]", "");

        if (!digits.matches("\\d{7,15}")) {
            throw new IllegalArgumentException("Invalid phone number: " + phone);
        }

        return digits;
    }
}