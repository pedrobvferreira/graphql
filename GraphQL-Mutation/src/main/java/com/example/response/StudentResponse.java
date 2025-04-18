package com.example.response;

import com.example.entity.Student;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentResponse {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String street;
	private String city;

	// this is for internal use. DO NOT PUT IN SCHEMA
	@JsonIgnore
	private Student student;
	
	public StudentResponse (Student student) {
		this.student = student;
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
