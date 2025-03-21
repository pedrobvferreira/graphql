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
	public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, @Argument String subjectNameFilter) {
		if (studentResponse.getStudent() == null || studentResponse.getStudent().getLearningSubjects() == null) {
			return List.of();
		}

		SubjectNameFilter filter = SubjectNameFilter.fromString(subjectNameFilter);
		return studentResponse.getStudent().getLearningSubjects().stream()
				.filter(subject -> filter == SubjectNameFilter.ALL ||
						subject.getSubjectName().equalsIgnoreCase(filter.name()))
				.map(SubjectResponse::new)
				.collect(Collectors.toList());
	}

}