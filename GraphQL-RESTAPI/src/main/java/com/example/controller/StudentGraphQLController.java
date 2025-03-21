package com.example.controller;

import com.example.request.StudentRequest;
import com.example.response.StudentResponse;
import com.example.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class StudentGraphQLController {
    private final StudentService studentService;

    @QueryMapping
    public StudentResponse getStudent(@Argument Long id) {
        return new StudentResponse(studentService.getStudentById(id));
    }

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
