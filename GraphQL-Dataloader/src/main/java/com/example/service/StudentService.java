package com.example.service;

import com.example.dto.AddressDTO;
import com.example.dto.StudentDTO;
import com.example.request.StudentRequest;
import com.example.response.SubjectResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class StudentService {

	// Dados fictícios
	private final Map<Long, StudentDTO> students = new HashMap<>();

	public StudentService() {
		// Inicializar dados fictícios diretamente
		students.put(1L, new StudentDTO(1L,
				"João",
				"Silva",
				"joao@example.com",
				new AddressDTO(1L, "Rua A", "Lisboa"),
				List.of(
						new SubjectResponse(1L, "JAVA", 95.0),
						new SubjectResponse(2L, "MYSQL", 85.0)
				)
		));

		students.put(2L, new StudentDTO(2L,
				"Maria",
				"Costa",
				"maria@example.com",
				new AddressDTO(2L, "Rua B", "Porto"),
				List.of(
						new SubjectResponse(3L, "MONGODB", 90.0)
				)
		));
	}

	public StudentDTO getStudentById(Long id) {
		return students.get(id);
	}

	public StudentDTO createStudent(StudentRequest request) {
		long newId = students.size() + 1L;
		StudentDTO newStudent = new StudentDTO(
				newId,
				request.getFirstName(),
				request.getLastName(),
				request.getEmail(),
				new AddressDTO(newId, request.getStreet(), request.getCity()),
				request.getLearningSubjects().stream()
						.map(sr -> new SubjectResponse(
								new Random().nextLong(),
								sr.getSubjectName(),
								sr.getMarksObtained())
						).toList()
		);
		students.put(newId, newStudent);
		return newStudent;
	}

	public StudentDTO updateStudent(Long id, StudentRequest request) {
		StudentDTO student = students.get(id);
		if (student != null) {
			student.setFirstName(request.getFirstName());
			student.setLastName(request.getLastName());
			student.setEmail(request.getEmail());
			student.setAddress(new AddressDTO(id, request.getStreet(), request.getCity()));

			if (request.getLearningSubjects() != null) {
				student.setLearningSubjects(
						request.getLearningSubjects().stream()
								.map(sr -> new SubjectResponse(
										new Random().nextLong(),
										sr.getSubjectName(),
										sr.getMarksObtained())
								).toList()
				);
			}
		}
		return student;
	}

	public Boolean deleteStudent(Long id) {
		return students.remove(id) != null;
	}
}
