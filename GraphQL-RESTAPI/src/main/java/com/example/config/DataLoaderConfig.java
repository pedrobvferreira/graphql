package com.example.config;

import com.example.dataLoader.SubjectDataLoader;
import com.example.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
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
            System.out.println("Registrando subjectDataLoader...");
            var loader = DataLoaderFactory.newMappedDataLoader(subjectDataLoader.getLoader());
            registry.register("subjectDataLoader", loader);
        };
    }

}
