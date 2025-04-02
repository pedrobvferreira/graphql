package com.example.dataLoader;

import com.example.entity.Subject;
import com.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.dataloader.MappedBatchLoaderWithContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubjectDataLoader {

    private final SubjectRepository subjectRepository;

    public MappedBatchLoader<Long, List<Subject>> getLoader() {
        return studentIds -> CompletableFuture.supplyAsync(() -> {
            List<Subject> subjects = subjectRepository.findByStudentIds(studentIds);
            return subjects.stream()
                    .collect(Collectors.groupingBy(subject -> subject.getStudent().getId()));
        });
    }
}
