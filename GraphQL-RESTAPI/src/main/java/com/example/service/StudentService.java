package com.example.service;

import com.example.dto.StudentDTO;
import com.example.entity.Address;
import com.example.entity.Student;
import com.example.entity.Subject;
import com.example.repository.AddressRepository;
import com.example.repository.StudentRepository;
import com.example.repository.SubjectRepository;
import com.example.request.StudentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

	@Autowired
	AddressRepository addressRepository;
	
	@Autowired
	SubjectRepository subjectRepository;
	
	@Autowired
	StudentRepository studentRepository;

	public StudentDTO getStudentById(Long id) {
		Student student = studentRepository.findById(id).orElse(null);
		return new StudentDTO(student);
	}

	public StudentDTO createStudent(StudentRequest request) {
		Student student = new Student();
		student.setFirstName(request.getFirstName());
		student.setLastName(request.getLastName());
		student.setEmail(request.getEmail());

		Address address = new Address();
		address.setStreet(request.getStreet());
		address.setCity(request.getCity());
		address = addressRepository.save(address);
		student.setAddress(address);

		student = studentRepository.save(student);
		
		if(request.getLearningSubjects() != null) {
			Student finalStudent = student;
			List<Subject> subjectsList = request.getLearningSubjects().stream().map(subjectRequest -> {
				Subject subject = new Subject();
				subject.setSubjectName(subjectRequest.getSubjectName().toUpperCase());
				subject.setMarksObtained(subjectRequest.getMarksObtained());
				subject.setStudent(finalStudent);
				return subject;
			}).collect(Collectors.toList());

			subjectRepository.saveAll(subjectsList);
			student.setLearningSubjects(subjectsList);
		}

		return new StudentDTO(student);
	}

	public StudentDTO updateStudent(Long id, StudentRequest request) {
		Student student = studentRepository.findById(id).orElse(null);
		if (student != null) {
			if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
			if (request.getLastName() != null) student.setLastName(request.getLastName());
			if (request.getEmail() != null) student.setEmail(request.getEmail());

			Address address = student.getAddress();
			address.setStreet(request.getStreet());
			address.setCity(request.getCity());

			address = addressRepository.save(address);
			student.setAddress(address);

			student = studentRepository.save(student);

			// Atualizar assuntos (Subjects)
			if (request.getLearningSubjects() != null) {
				Student finalStudent = student;
				List<Subject> subjectsList = request.getLearningSubjects().stream().map(subjectRequest -> {
					Subject subject = new Subject();
					subject.setSubjectName(subjectRequest.getSubjectName());
					subject.setMarksObtained(subjectRequest.getMarksObtained());
					subject.setStudent(finalStudent);
					return subject;
				}).collect(Collectors.toList());

				// Remover os assuntos antigos e salvar os novos
				subjectRepository.deleteAll(student.getLearningSubjects());
				subjectRepository.saveAll(subjectsList);
				student.setLearningSubjects(subjectsList);
			}
		}
		return new StudentDTO(student);
	}

	public Boolean deleteStudent(Long id) {
		if (!studentRepository.existsById(id)) {
			return false;
		}

		Student student = studentRepository.findById(id).orElse(null);
		if (student != null) {
			// Apagar os assuntos (Subjects)
			subjectRepository.deleteAll(student.getLearningSubjects());
			// Apagar o endereço (Address)
			if (student.getAddress() != null) {
				addressRepository.delete(student.getAddress());
			}
			// Apagar o próprio estudante
			studentRepository.delete(student);
			return true;
		}

		return false;
	}
}
