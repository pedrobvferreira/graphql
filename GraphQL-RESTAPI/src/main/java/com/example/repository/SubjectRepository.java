package com.example.repository;

import com.example.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByStudentId(Long studentId);

    List<Subject> findByStudentIdAndSubjectName(Long studentId, String subjectName);

<<<<<<< HEAD
    @Query("SELECT s FROM Subject s JOIN s.student st WHERE st.id IN :studentIds")
=======
    @Query("SELECT DISTINCT s FROM Subject s JOIN s.student st WHERE st.id IN :studentIds")
>>>>>>> 116c3ed (fix)
    List<Subject> findByStudentIds(@Param("studentIds") Set<Long> studentIds);
}
