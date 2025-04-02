package com.example.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class StudentRequest {

	private String firstName;
	private String lastName;
	private String email;
	private String street;
	private String city;
	private List<SubjectRequest> learningSubjects;

}
