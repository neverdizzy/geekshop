/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.exception;

import graphql.ErrorType;

/**
 * This exception should be thrown when some unexpected and exceptional case is encountered.
 *
 * Created on Nov, 2020 by @author bobo
 */
public class InternalServerError extends AbstractGraphqlException {
    public InternalServerError() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public InternalServerError(ErrorCode errorCode) {
        super(errorCode);
    }

    public InternalServerError(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.ExecutionAborted;
    }
}
