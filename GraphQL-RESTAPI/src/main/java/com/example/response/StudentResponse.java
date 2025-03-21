package com.example.response;

import java.util.List;

import com.example.entity.Student;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentResponse {

	private long id;
	private String firstName;
	private String lastName;
	private String email;
	private String street;
	private String city;
	private List<SubjectResponse> learningSubjects;
	
	public StudentResponse (Student student) {
		this.id = student.getId();
		this.firstName = student.getFirstName();
		this.lastName = student.getLastName();
		this.email = student.getEmail();

		if (student.getAddress() != null) {
			this.street = student.getAddress().getStreet();
			this.city = student.getAddress().getCity();
		}
	}

}
