package com.example.resolver;

import com.example.entity.Subject;
import com.example.enums.SubjectNameFilter;
import com.example.response.StudentResponse;
import com.example.response.SubjectResponse;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StudentResponseResolver {

	@SchemaMapping(typeName = "StudentResponse", field = "learningSubjects")
	public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, @Argument SubjectNameFilter subjectNameFilter) {

		List<SubjectResponse> learningSubjects = new ArrayList<>();
		if (studentResponse.getStudent() != null && studentResponse.getStudent().getLearningSubjects() != null) {
			for (Subject subject : studentResponse.getStudent().getLearningSubjects()) {
				if ("ALL".equalsIgnoreCase(subjectNameFilter.name()) || subject.getSubjectName().equalsIgnoreCase(subjectNameFilter.name())) {
					learningSubjects.add(new SubjectResponse(subject));
				}
			}
		}
		return learningSubjects;
	}

	@SchemaMapping(typeName = "StudentResponse", field = "fullName")
	public String getFullName(StudentResponse studentResponse) {
		return studentResponse.getFirstName() + " " + studentResponse.getLastName();
	}
}