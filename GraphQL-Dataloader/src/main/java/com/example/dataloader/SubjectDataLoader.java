package com.example.dataloader;

import com.example.response.SubjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.MappedBatchLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SubjectDataLoader implements MappedBatchLoader<Long, List<SubjectResponse>> {

    @Override
    public CompletionStage<Map<Long, List<SubjectResponse>>> load(Set<Long> studentIds) {
        log.info(Thread.currentThread().getName());
        Map<Long, List<SubjectResponse>> subjectsMap = Map.of(
                1L, List.of(
                        new SubjectResponse(1L, "JAVA", 90.0),
                        new SubjectResponse(2L, "MYSQL", 85.5),
                        new SubjectResponse(3L, "MONGODB", 88.0)
                ),
                2L, List.of(
                        new SubjectResponse(4L, "JAVA", 92.5),
                        new SubjectResponse(5L, "MYSQL", 78.0)
                )
        );

        Map<Long, List<SubjectResponse>> result = subjectsMap.entrySet().stream()
                .filter(entry -> studentIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return CompletableFuture.completedFuture(result);
    }
}
