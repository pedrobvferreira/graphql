package com.example.dataLoader;

import com.example.entity.Subject;
import com.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoaderWithContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubjectDataLoader {

    private final SubjectRepository subjectRepository;

    public MappedBatchLoaderWithContext<Long, List<Subject>> getLoader() {
        return (studentIds, env) -> CompletableFuture.supplyAsync(() -> {
            List<Subject> subjects = subjectRepository.findByStudentIds(studentIds);

            // Retorna um Map<studentId, List<Subject>>
            return subjects.stream().collect(Collectors.groupingBy(
                    subject -> subject.getStudent().getId()
            ));
        });
    }
}
