package com.skripok.bloodpressure.person.web.enums;

public enum PhoneCountry {

    UKRAINE("Україна", "380"),
    POLAND("Польща", "48"),
    CZECHIA("Чехія", "420"),
    ROMANIA("Румунія", "40"),
    MOLDOVA("Молдова", "373"),
    BULGARIA("Болгарія", "359"),
    LITHUANIA("Литва", "370"),
    LATVIA("Латвія", "371"),
    ESTONIA("Естонія", "372");

    private final String displayName;
    private final String code;

    PhoneCountry(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }
}