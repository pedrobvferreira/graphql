package com.example.response;

import com.example.dto.AddressDTO;
import com.example.dto.StudentDTO;
import com.example.entity.Subject;
import com.example.enums.SubjectNameFilter;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@GraphQLType
@Setter
@Getter
public class StudentResponse {

	private long id;
	private String firstName;
	private String lastName;
	private String email;
	private AddressDTO address;

	// Interno - não exposto diretamente
	// usado para filtrar posteriormente
	@GraphQLIgnore
	private List<SubjectResponse> rawSubjects;
	
	public StudentResponse (StudentDTO student) {
		this.id = student.getId();
		this.firstName = student.getFirstName();
		this.lastName = student.getLastName();
		this.email = student.getEmail();
		this.rawSubjects = student.getLearningSubjects();
	}

	// Exposto via GraphQL com filtro
	@GraphQLQuery(name = "learningSubjects")
	public List<SubjectResponse> getLearningSubjects(
			@GraphQLArgument(name = "subjectNameFilter") SubjectNameFilter subjectNameFilter
	) {
		if (rawSubjects == null) return List.of();

		return rawSubjects.stream()
				.filter(subject -> {
					if (subjectNameFilter == null || subjectNameFilter == SubjectNameFilter.All) return true;
					return Objects.equals(subject.getSubjectName(), subjectNameFilter.name());
				})
				.collect(Collectors.toList());
	}

}
