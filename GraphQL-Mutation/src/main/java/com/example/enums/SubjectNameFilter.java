package com.example.enums;

import java.util.Arrays;

public enum SubjectNameFilter {
    All, Java, MySQL, MongoDB;

    public static SubjectNameFilter fromString(String value) {
        return Arrays.stream(SubjectNameFilter.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(All);
    }
}
