package dev.besi.gazdabolt.backend.inventory.error;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

public class IllegalArgumentError extends RuntimeException implements GraphQLError {
    
    public IllegalArgumentError(String message) {
        super(message);
    }

    public IllegalArgumentError(Throwable cause) {
        super(cause.getMessage(), cause);
    }
    
    public IllegalArgumentError(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.DataFetchingException;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getMessage();
    }
}
