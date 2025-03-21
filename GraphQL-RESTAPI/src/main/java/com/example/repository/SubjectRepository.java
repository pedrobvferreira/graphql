package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Subject;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByStudentId(Long studentId);

    List<Subject> findByStudentIdAndSubjectName(Long studentId, String subjectName);
}
