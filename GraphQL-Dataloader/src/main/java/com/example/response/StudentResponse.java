package com.example.response;

import com.example.dto.StudentDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentResponse {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	
	public StudentResponse (StudentDTO student) {
		this.id = student.getId();
		this.firstName = student.getFirstName();
		this.lastName = student.getLastName();
		this.email = student.getEmail();
	}
}
