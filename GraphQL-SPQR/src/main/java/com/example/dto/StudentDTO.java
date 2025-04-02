package com.example.dto;

import com.example.entity.Student;
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

    public StudentDTO(Student student) {
        if (student == null) return;
        this.id = student.getId();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.email = student.getEmail();

        if (student.getAddress() != null) {
            this.address = new AddressDTO(student.getAddress());
        }

        if (student.getLearningSubjects() != null) {
            this.learningSubjects = student.getLearningSubjects().stream()
                    .map(SubjectResponse::new)
                    .toList();
        }
    }
}

