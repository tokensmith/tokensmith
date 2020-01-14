package net.toknsmith.login.exception.http.openid;

import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;

import java.util.Optional;


/**
 * The exception that occurs when a non 200 status code is received from /token.
 */
public class ErrorResponseException extends Exception {
    protected Integer statusCode;
    private Optional<TokenErrorResponse> errorResponse;

    public ErrorResponseException(Optional<TokenErrorResponse> errorResponse, Integer statusCode) {
        super();
        this.errorResponse = errorResponse;
        this.statusCode = statusCode;
    }

    public ErrorResponseException(String message, Throwable cause, Integer statusCode, Optional<TokenErrorResponse> errorResponse) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public Optional<TokenErrorResponse> getErrorResponse() {
        return errorResponse;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("ErrorResponseException { ")
                .append("statusCode = ").append(statusCode)
                .append(", errorResponse = ").append(errorResponse)
                .append(" }").toString();
    }
}
