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
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class StudentResponseResolver {

	private final SubjectRepository subjectRepository;
	private final AddressRepository addressRepository;

	/*@SchemaMapping(typeName = "StudentResponse", field = "learningSubjects")
	public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, @Argument String subjectNameFilter) {
		// 1. Ler argumento do enum
		SubjectNameFilter filter = SubjectNameFilter.fromString(subjectNameFilter);

		List<Subject> subjects;

		// 2. Buscar os subjects com ou sem filtro
		if (filter == SubjectNameFilter.ALL) {
			// Se for ALL, traz todas as disciplinas do estudante
			subjects = subjectRepository.findByStudentId(studentResponse.getId());
		} else {
			// Caso contrário, filtra diretamente no banco pelo nome da disciplina
			subjects = subjectRepository.findByStudentIdAndSubjectName(studentResponse.getId(), filter.name().toUpperCase());
		}

		// 3. Retornar mapeado
		return subjects.stream()
				.map(SubjectResponse::new)
				.collect(Collectors.toList());
	}*/

	@SchemaMapping(typeName = "StudentResponse", field = "learningSubjects")
	public CompletableFuture<List<SubjectResponse>> getLearningSubjects(
			StudentResponse studentResponse,
			@Argument SubjectNameFilter filter,
			DataFetchingEnvironment env) {

		DataLoader<Long, List<Subject>> subjectDataLoader = env.getDataLoader("subjectDataLoader");
		if (subjectDataLoader == null) {
			throw new IllegalStateException("DataLoader 'subjectDataLoader' não registrado.");
		}

		return subjectDataLoader.load(studentResponse.getId())
				.thenApply(subjects -> subjects.stream()
						.filter(subject -> subject.getSubjectName().equalsIgnoreCase(filter.name()))
						.map(SubjectResponse::new)
						.collect(Collectors.toList())
				);
	}

	@SchemaMapping(typeName = "StudentResponse", field = "address")
	public AddressResponse getAddress(StudentResponse studentResponse) {
		Address address = addressRepository.findByStudentId(studentResponse.getId());
		return address != null ? new AddressResponse(new AddressDTO(address)) : null;
	}

}