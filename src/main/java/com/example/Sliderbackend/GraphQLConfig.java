package com.example.Sliderbackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import graphql.schema.idl.TypeRuntimeWiring;
import graphql.schema.GraphQLScalarType;
import graphql.Scalars;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            // Add type extensions
            wiringBuilder.type("Query", this::configureQueryType);

            // Add any scalar definitions if needed
            // wiringBuilder.scalar(customScalarType);
        };
    }

    private TypeRuntimeWiring.Builder configureQueryType(TypeRuntimeWiring.Builder builder) {
        return builder.dataFetcher("findSharedInterests",
                env -> {
                    // This is just a placeholder - your actual implementation is in your resolver
                    // This ensures the schema validator knows about this field
                    return null;
                });
    }
}