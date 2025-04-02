package com.example.response;

import com.example.dto.AddressDTO;
import com.example.dto.StudentDTO;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@GraphQLType
@Setter
@Getter
public class StudentResponse {

	private long id;
	private String firstName;
	private String lastName;
	private String email;
	private AddressDTO address;
	private List<SubjectResponse> learningSubjects;
	
	public StudentResponse (StudentDTO student) {
		this.id = student.getId();
		this.firstName = student.getFirstName();
		this.lastName = student.getLastName();
		this.email = student.getEmail();
	}

}
