package com.example.rest;

import com.example.request.StudentRequest;
import com.example.response.StudentResponse;
import com.example.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@AllArgsConstructor
public class StudentRestController {

    private final StudentService studentService;

    @GetMapping("/{id}")
    public StudentResponse getStudent(@PathVariable Long id) {
        return new StudentResponse(studentService.getStudentById(id));
    }

    @PostMapping
    public StudentResponse createStudent(@RequestBody StudentRequest studentRequest) {
        return new StudentResponse(studentService.createStudent(studentRequest));
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudent(@PathVariable Long id, @RequestBody StudentRequest studentRequest) {
        return new StudentResponse(studentService.updateStudent(id, studentRequest));
    }

    @DeleteMapping("/{id}")
    public Boolean deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }
}
