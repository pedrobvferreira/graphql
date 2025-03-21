package com.example.resolver;

import com.example.dto.AddressDTO;
import com.example.entity.Address;
import com.example.entity.Subject;
import com.example.enums.SubjectNameFilter;
import com.example.repository.AddressRepository;
import com.example.repository.SubjectRepository;
import com.example.response.AddressResponse;
import com.example.response.StudentResponse;
import com.example.response.SubjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class StudentResponseResolver {

	private final SubjectRepository subjectRepository;
	private final AddressRepository addressRepository;

	@SchemaMapping(typeName = "StudentResponse", field = "learningSubjects")
	public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, @Argument String subjectNameFilter) {
		SubjectNameFilter filter = SubjectNameFilter.fromString(subjectNameFilter);

		List<Subject> subjects;

		if (filter == SubjectNameFilter.ALL) {
			// Se for ALL, traz todas as disciplinas do estudante
			subjects = subjectRepository.findByStudentId(studentResponse.getId());
		} else {
			// Caso contrário, filtra diretamente no banco pelo nome da disciplina
			subjects = subjectRepository.findByStudentIdAndSubjectName(studentResponse.getId(), filter.name().toUpperCase());
		}

		return subjects.stream()
				.map(SubjectResponse::new)
				.collect(Collectors.toList());
	}

	@SchemaMapping(typeName = "StudentResponse", field = "address")
	public AddressResponse getAddress(StudentResponse studentResponse) {
		Address address = addressRepository.findByStudentId(studentResponse.getId());
		return address != null ? new AddressResponse(new AddressDTO(address)) : null;
	}

}