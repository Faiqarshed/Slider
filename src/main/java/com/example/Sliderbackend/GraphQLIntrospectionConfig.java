package com.example.Sliderbackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.graphql.execution.GraphQlSource;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Configuration
public class GraphQLIntrospectionConfig {

    // This creates the GraphQLSchema bean that was missing
    @Bean
    public GraphQLSchema graphQLSchema() throws IOException {
        // Load schema from resources
        Resource schemaResource = new ClassPathResource("graphql/schema.graphqls");
        String sdl = Files.readString(schemaResource.getFile().toPath(), StandardCharsets.UTF_8);

        // Parse the schema
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);

        // Create runtime wiring
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder ->
                        builder.dataFetcher("findSharedInterests", environment -> {
                            // This is just a placeholder for schema validation
                            // Your actual implementation is in your resolver
                            return null;
                        })
                )
                .build();

        // Generate the schema
        return new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    @Bean
    public GraphQL graphQL(GraphQLSchema graphQLSchema) {
        return GraphQL.newGraphQL(graphQLSchema)
                .build();
    }
}