package com.example.resolver;

import com.example.enums.SubjectNameFilter;
import com.example.response.StudentResponse;
import com.example.response.SubjectResponse;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StudentResponseResolver {

	@SchemaMapping(typeName = "StudentResponse", field = "learningSubjects")
	public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, @Argument SubjectNameFilter subjectNameFilter) {
		if (studentResponse.getLearningSubjects() == null) {
			return List.of();
		}

		return studentResponse.getLearningSubjects().stream()
				.filter(subject -> subjectNameFilter == SubjectNameFilter.ALL ||
						subject.getSubjectName().equalsIgnoreCase(subjectNameFilter.name()))
				.collect(Collectors.toList());
	}

}