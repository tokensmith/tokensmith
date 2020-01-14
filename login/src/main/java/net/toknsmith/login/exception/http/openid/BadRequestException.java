package net.toknsmith.login.exception.http.openid;

import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;
import net.toknsmith.login.http.StatusCode;

import java.util.Optional;

public class BadRequestException extends ErrorResponseException {

    public BadRequestException(Optional<TokenErrorResponse> errorResponse) {
        super(errorResponse, StatusCode.BAD_REQUEST.getCode());
    }

    public BadRequestException(TokenErrorResponse error) {
        super(Optional.of(error), StatusCode.BAD_REQUEST.getCode());
    }
}
