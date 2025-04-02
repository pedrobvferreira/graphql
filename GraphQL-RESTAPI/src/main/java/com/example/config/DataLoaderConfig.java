package com.example.config;

import com.example.dataLoader.SubjectDataLoader;
import com.example.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.dataloader.MappedBatchLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataLoaderRegistrar;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class DataLoaderConfig {

    private final SubjectDataLoader subjectDataLoader;

    @Bean
    public DataLoaderRegistrar dataLoaderRegistrar() {
        return (registry, context) -> {
            DataLoader<Long, List<Subject>> subjectDataLoaderInstance =
                    DataLoaderFactory.newMappedDataLoader(subjectDataLoader.getLoader());

            registry.register("subjectDataLoader", subjectDataLoaderInstance);
        };
    }

}
