package com.example.dto;

import com.example.response.SubjectResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private AddressDTO address;
    private List<SubjectResponse> learningSubjects;

}

