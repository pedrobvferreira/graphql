package com.example.graphql;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchemaController {

    private static final String SDL = """
        type Query {
            firstQuery: String
            secondQuery: String
            fullName(sampleRequest: SampleRequest): String
        }

        input SampleRequest {
            firstName: String!
            lastName: String
        }
        """;

    @GetMapping(value = "/sdl", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getSchema() {
        GraphQLSchema schema = buildSchemaFromSDL(SDL);
        return printSchema(schema);
    }

    private GraphQLSchema buildSchemaFromSDL(String sdl) {
        // Parse SDL
        TypeDefinitionRegistry registry = new SchemaParser().parse(sdl);
        // Wiring (vazio, não vamos resolver nada, só imprimir schema)
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring().build();
        // Schema final
        return new SchemaGenerator().makeExecutableSchema(registry, wiring);
    }
    private String printSchema(GraphQLSchema schema) {
        // Imprimir
        SchemaPrinter printer = new SchemaPrinter(SchemaPrinter.Options.defaultOptions()
                .includeSchemaDefinition(true)
                .includeScalarTypes(true)
                .includeDirectives(false)
                .includeIntrospectionTypes(false));
        return printer.print(schema);
    }

}
