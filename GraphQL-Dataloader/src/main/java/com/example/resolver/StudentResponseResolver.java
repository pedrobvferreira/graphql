package com.example.resolver;

import com.example.enums.SubjectNameFilter;
import com.example.response.AddressResponse;
import com.example.response.StudentResponse;
import com.example.response.SubjectResponse;
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

	@SchemaMapping(typeName = "StudentResponse", field = "address")
	public CompletableFuture<AddressResponse> getAddress(StudentResponse studentResponse,
														 DataLoader<Long, AddressResponse> addressDataLoader) {
		return addressDataLoader.load(studentResponse.getId());
	}

	@SchemaMapping(typeName = "StudentResponse", field = "learningSubjects")
	public CompletableFuture<List<SubjectResponse>> getLearningSubjects(StudentResponse studentResponse,
																		@Argument SubjectNameFilter filter,
																		DataLoader<Long, List<SubjectResponse>> subjectDataLoader) {
		return subjectDataLoader.load(studentResponse.getId())
				.thenApply(subjects -> {
					if (filter == null || filter == SubjectNameFilter.All) {
						return subjects;
					}
					return subjects.stream()
							.filter(s -> s.getSubjectName().equalsIgnoreCase(filter.name()))
							.collect(Collectors.toList());
				});
	}

}