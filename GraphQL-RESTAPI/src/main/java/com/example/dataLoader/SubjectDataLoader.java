package com.example.dataLoader;

import com.example.entity.Subject;
import com.example.repository.SubjectRepository;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class SubjectDataLoader {

    @Autowired
    private SubjectRepository subjectRepository;

    public MappedBatchLoader<Long, List<Subject>> getLoader() {
        return studentIds -> CompletableFuture.supplyAsync(() -> {
            List<Subject> subjects = subjectRepository.findByStudentIds(studentIds);
            return subjects.stream()
                    .collect(Collectors.groupingBy(subject -> subject.getStudent().getId()));
        });
    }
}
