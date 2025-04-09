package com.example.resolver;

import com.example.dto.AddressDTO;
import com.example.enums.SubjectNameFilter;
import com.example.response.AddressResponse;
import com.example.response.StudentResponse;
import com.example.response.SubjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class StudentResponseResolver {

	// Dados fictícios para Subjects, agrupados por studentId
	private final Map<Long, List<SubjectResponse>> mockSubjects = Map.of(
			1L, List.of(
					new SubjectResponse(1L, "JAVA", 90.0),
					new SubjectResponse(2L, "MYSQL", 85.5),
					new SubjectResponse(3L, "MONGODB", 88.0)
			),
			2L, List.of(
					new SubjectResponse(4L, "JAVA", 92.5),
					new SubjectResponse(5L, "MYSQL", 78.0)
			)
	);

	// Dados fictícios para Endereços, por studentId
	private final Map<Long, AddressResponse> mockAddresses = Map.of(
			1L, new AddressResponse(new AddressDTO(1L, "Rua das Flores", "Lisboa")),
			2L, new AddressResponse(new AddressDTO(2L, "Avenida Central", "Porto"))
	);

	@SchemaMapping(typeName = "StudentResponse", field = "learningSubjects")
	public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, @Argument SubjectNameFilter filter) {

		List<SubjectResponse> subjects = mockSubjects.get(studentResponse.getId());

		if (subjects == null) {
			return List.of(); // Retorna lista vazia se não houver dados
		}

		if (filter == null || filter == SubjectNameFilter.All) {
			return subjects;
		}

		// Filtra a lista fictícia
		return subjects.stream()
				.filter(subject -> subject.getSubjectName().equalsIgnoreCase(filter.name()))
				.collect(Collectors.toList());
	}

	@SchemaMapping(typeName = "StudentResponse", field = "address")
	public AddressResponse getAddress(StudentResponse studentResponse) {
		return mockAddresses.get(studentResponse.getId());
	}

}