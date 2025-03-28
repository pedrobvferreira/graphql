package com.example.config;

import com.example.dataLoader.SubjectDataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoaderConfig {

    @Autowired
    private SubjectDataLoader subjectDataLoader;

    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();
        registry.register("subjectDataLoader",
                DataLoaderFactory.newMappedDataLoader(subjectDataLoader.getLoader()));
        return registry;
    }
}
