package com.example.mutation;

import com.example.request.StudentRequest;
import com.example.response.StudentResponse;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class Mutation {

	@Autowired
	StudentService studentService;

	@MutationMapping
	public StudentResponse createStudent(@Argument StudentRequest studentRequest) {
		return new StudentResponse(studentService.createStudent(studentRequest));
	}

	@MutationMapping
	public StudentResponse updateStudent(@Argument Long id, @Argument StudentRequest studentRequest) {
		return new StudentResponse(studentService.updateStudent(id, studentRequest));
	}

	@MutationMapping
	public Boolean deleteStudent(@Argument Long id) {
		return studentService.deleteStudent(id);
	}
}
