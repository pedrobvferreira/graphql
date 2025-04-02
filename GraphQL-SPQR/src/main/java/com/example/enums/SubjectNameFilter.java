package com.example.enums;

import io.leangen.graphql.annotations.types.GraphQLType;

import java.util.Arrays;

@GraphQLType
public enum SubjectNameFilter {
    All, Java, MySQL, MongoDB;

    public static SubjectNameFilter fromString(String value) {
        return Arrays.stream(SubjectNameFilter.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(All);
    }
}
