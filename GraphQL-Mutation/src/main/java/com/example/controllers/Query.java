package com.example.controllers;

import com.example.request.SampleRequest;
import com.example.response.StudentResponse;
import com.example.service.StudentService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class Query {

    @Autowired
    StudentService studentService;

    @QueryMapping
    public String firstQuery() {
        return "First Query";
    }

    @QueryMapping
    public String secondQuery() {
        return "Second Query";
    }

    @QueryMapping
    public String fullName(@Argument SampleRequest sampleRequest) {
        return sampleRequest.getFirstName() + " " + sampleRequest.getLastName();
    }

    @QueryMapping
    public StudentResponse getStudent(@Argument @NotNull Long id) {
        return new StudentResponse(studentService.getStudentById(id));
    }
}
