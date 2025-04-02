package com.example.conf;

import com.example.service.StudentService;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GraphQLProvider {

    private final StudentService studentService;

    @Bean
    public GraphQL graphQL() {
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withBasePackages("com.example")
                .withOperationsFromSingleton(studentService)
                .generate();
        return GraphQL.newGraphQL(schema).build();
    }
}
