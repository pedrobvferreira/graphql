package com.example.config;

import com.example.dataloader.AddressDataLoader;
import com.example.dataloader.SubjectDataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoaderConfig {

    @Bean
    public DataLoaderRegistry dataLoaderRegistry(AddressDataLoader addressDataLoader,
                                                 SubjectDataLoader subjectDataLoader) {
        DataLoaderRegistry registry = new DataLoaderRegistry();
        registry.register("addressDataLoader", DataLoaderFactory.newMappedDataLoader(addressDataLoader));
        registry.register("subjectDataLoader", DataLoaderFactory.newMappedDataLoader(subjectDataLoader));
        return registry;
    }
}
