package com.example.config;

import com.example.dataLoader.SubjectDataLoader;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataLoaderRegistrar;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.stereotype.Component;

@Configuration
@RequiredArgsConstructor
public class GraphQLConfiguration {

    private final SubjectDataLoader subjectDataLoader;

    @Bean
    public DataLoaderRegistrar dataLoaderRegistrar() {
        return (registry, context) -> {
            registry.register(
                    "subjectDataLoader",
                    DataLoaderFactory.newMappedDataLoader(
                        subjectDataLoader.getLoader()
                    )
            );
        };
    }

}
