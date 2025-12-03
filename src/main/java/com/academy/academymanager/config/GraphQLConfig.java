package com.academy.academymanager.config;

import com.academy.academymanager.graphql.scalar.BigDecimalScalar;
import com.academy.academymanager.graphql.scalar.DateTimeScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * GraphQL configuration.
 * Registers custom scalars and configures runtime wiring.
 */
@Configuration
public class GraphQLConfig {
    /**
     * Configures GraphQL runtime wiring with custom scalars.
     * 
     * Scalars registered:
     * - DateTime: Custom scalar for LocalDateTime (ISO-8601 format)
     * - BigDecimal: Custom scalar for precise decimal numbers
     * 
     * @return runtime wiring configurer
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(DateTimeScalar.build())
                .scalar(BigDecimalScalar.build());
    }
}

