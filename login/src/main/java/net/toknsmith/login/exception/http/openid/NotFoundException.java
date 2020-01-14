package net.toknsmith.login.exception.http.openid;

import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;
import net.toknsmith.login.http.StatusCode;

import java.util.Optional;

public class NotFoundException extends ErrorResponseException {
    public NotFoundException(Optional<TokenErrorResponse> errorResponse) {
        super(errorResponse, StatusCode.NOT_FOUND.getCode());
    }

    public NotFoundException(TokenErrorResponse error) {
        super(Optional.of(error), StatusCode.NOT_FOUND.getCode());
    }
}
