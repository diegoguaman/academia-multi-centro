package com.academy.academymanager.graphql.scalar;

import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Custom scalar for LocalDateTime in GraphQL.
 * Serializes/deserializes LocalDateTime to/from ISO-8601 string format.
 * 
 * Implementation: Uses basic Coercing interface compatible with all graphql-java versions.
 */
@Component
public class DateTimeScalar {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public static GraphQLScalarType build() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("Custom scalar for LocalDateTime (ISO-8601 format: yyyy-MM-dd'T'HH:mm:ss)")
                .coercing(new Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(final Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).format(FORMATTER);
                        }
                        throw new CoercingSerializeException(
                                "Expected LocalDateTime but got: " + 
                                (dataFetcherResult != null ? dataFetcherResult.getClass().getSimpleName() : "null")
                        );
                    }
                    
                    @Override
                    public LocalDateTime parseValue(final Object input) throws CoercingParseValueException {
                        if (input instanceof String) {
                            try {
                                return LocalDateTime.parse((String) input, FORMATTER);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseValueException(
                                        "Invalid LocalDateTime format. Expected ISO-8601 format (yyyy-MM-dd'T'HH:mm:ss), got: " + input, 
                                        e
                                );
                            }
                        }
                        throw new CoercingParseValueException(
                                "Expected String but got: " + 
                                (input != null ? input.getClass().getSimpleName() : "null")
                        );
                    }
                    
                    @Override
                    public LocalDateTime parseLiteral(final Object input) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            try {
                                return LocalDateTime.parse(((StringValue) input).getValue(), FORMATTER);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(
                                        "Invalid LocalDateTime format. Expected ISO-8601 format (yyyy-MM-dd'T'HH:mm:ss), got: " + 
                                        ((StringValue) input).getValue(), 
                                        e
                                );
                            }
                        }
                        if (input instanceof Value) {
                            throw new CoercingParseLiteralException(
                                    "Expected StringValue but got: " + input.getClass().getSimpleName()
                            );
                        }
                        throw new CoercingParseLiteralException(
                                "Expected StringValue but got: " + 
                                (input != null ? input.getClass().getSimpleName() : "null")
                        );
                    }
                })
                .build();
    }
}
