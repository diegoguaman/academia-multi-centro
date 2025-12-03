package com.academy.academymanager.graphql.scalar;

import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Custom scalar for BigDecimal in GraphQL.
 * Handles conversion between BigDecimal and GraphQL numeric/string types.
 * 
 * Implementation: Uses basic Coercing interface compatible with all graphql-java versions.
 * Accepts String, Number, IntValue, FloatValue, and StringValue inputs.
 */
@Component
public class BigDecimalScalar {
    
    public static GraphQLScalarType build() {
        return GraphQLScalarType.newScalar()
                .name("BigDecimal")
                .description("Custom scalar for BigDecimal (precision decimal numbers)")
                .coercing(new Coercing<BigDecimal, String>() {
                    @Override
                    public String serialize(final Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof BigDecimal) {
                            return ((BigDecimal) dataFetcherResult).toPlainString();
                        }
                        if (dataFetcherResult instanceof Number) {
                            return BigDecimal.valueOf(((Number) dataFetcherResult).doubleValue()).toPlainString();
                        }
                        throw new CoercingSerializeException(
                                "Expected BigDecimal or Number but got: " + 
                                (dataFetcherResult != null ? dataFetcherResult.getClass().getSimpleName() : "null")
                        );
                    }
                    
                    @Override
                    public BigDecimal parseValue(final Object input) throws CoercingParseValueException {
                        if (input instanceof BigDecimal) {
                            return (BigDecimal) input;
                        }
                        if (input instanceof Number) {
                            return BigDecimal.valueOf(((Number) input).doubleValue());
                        }
                        if (input instanceof String) {
                            try {
                                return new BigDecimal((String) input);
                            } catch (NumberFormatException e) {
                                throw new CoercingParseValueException(
                                        "Invalid BigDecimal format: " + input, 
                                        e
                                );
                            }
                        }
                        throw new CoercingParseValueException(
                                "Expected BigDecimal, Number, or String but got: " + 
                                (input != null ? input.getClass().getSimpleName() : "null")
                        );
                    }
                    
                    @Override
                    public BigDecimal parseLiteral(final Object input) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            try {
                                return new BigDecimal(((StringValue) input).getValue());
                            } catch (NumberFormatException e) {
                                throw new CoercingParseLiteralException(
                                        "Invalid BigDecimal format: " + ((StringValue) input).getValue(), 
                                        e
                                );
                            }
                        }
                        if (input instanceof IntValue) {
                            return new BigDecimal(((IntValue) input).getValue());
                        }
                        if (input instanceof FloatValue) {
                            return ((FloatValue) input).getValue();
                        }
                        if (input instanceof Value) {
                            throw new CoercingParseLiteralException(
                                    "Expected StringValue, IntValue, or FloatValue but got: " + input.getClass().getSimpleName()
                            );
                        }
                        throw new CoercingParseLiteralException(
                                "Expected StringValue, IntValue, or FloatValue but got: " + 
                                (input != null ? input.getClass().getSimpleName() : "null")
                        );
                    }
                })
                .build();
    }
}
