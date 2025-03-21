package com.example.dto;

import com.example.entity.Student;
import lombok.*;

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

    public StudentDTO(Student student) {
        if (student == null) return;
        this.id = student.getId();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.email = student.getEmail();

        if (student.getAddress() != null) {
            this.address = new AddressDTO(student.getAddress());
        }
    }
}

