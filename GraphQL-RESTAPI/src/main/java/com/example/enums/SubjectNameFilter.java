package com.example.enums;

import org.apache.commons.lang3.StringUtils;

public enum SubjectNameFilter {
    ALL,
    JAVA,
    MYSQL,
    MONGODB;

    public static SubjectNameFilter fromString(String value) {
        if (StringUtils.isBlank(value)) {
            return ALL;
        }
        return SubjectNameFilter.valueOf(value.toUpperCase());
    }
}
