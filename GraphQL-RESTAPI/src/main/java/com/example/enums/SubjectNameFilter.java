package com.example.enums;

import java.util.Arrays;

public enum SubjectNameFilter {
    ALL,
    JAVA,
    MYSQL,
    MONGODB;

    public static SubjectNameFilter fromString(String value) {
        return Arrays.stream(SubjectNameFilter.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(ALL);
    }
}
