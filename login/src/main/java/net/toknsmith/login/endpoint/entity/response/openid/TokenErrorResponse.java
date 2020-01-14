package net.toknsmith.login.endpoint.entity.response.openid;

import java.util.Optional;

/**
 * The error response from  /token.
 */
public class TokenErrorResponse {
    private String error;
    private Optional<String> description = Optional.empty();

    public TokenErrorResponse() {
    }

    public TokenErrorResponse(String error, Optional<String> description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return new StringBuilder()
        .append("ErrorResponse { ")
                .append("error = '").append(error).append("\', ")
                .append("description = ").append(description)
                .append(" }")
                .toString();
    }
}