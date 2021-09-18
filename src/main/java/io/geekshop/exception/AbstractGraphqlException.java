/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on Nov, 2020 by @author bobo
 */
public class AbstractGraphqlException extends RuntimeException implements GraphQLError {
    private Map<String, Object> customAttributes = new LinkedHashMap<>();

    public AbstractGraphqlException(ErrorCode errorCode) {
        this(errorCode.defaultMessage, errorCode);
    }

    public AbstractGraphqlException(String errorMessage, ErrorCode errorCode) {
        super(errorMessage);
        customAttributes.put("errorCode", errorCode);
    }

    @Override
    public Map<String, Object> getExtensions() {
        return customAttributes;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.ValidationError;
    }
}
