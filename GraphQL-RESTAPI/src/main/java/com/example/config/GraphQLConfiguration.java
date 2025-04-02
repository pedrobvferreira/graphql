package com.example.config;

import com.example.dataLoader.SubjectDataLoader;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoaderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataLoaderRegistrar;


@Configuration
@RequiredArgsConstructor
public class GraphQLConfiguration {

    private final SubjectDataLoader subjectDataLoader;

    @Bean
    public DataLoaderRegistrar dataLoaderRegistrar() {
        return (registry, context) -> {
            registry.register(
               "subjectDataLoader",
               DataLoaderFactory.newMappedDataLoader(subjectDataLoader.getLoader())
            );
        };
    }

}
