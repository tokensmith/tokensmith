package net.toknsmith.login.exception.http.openid;

import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;
import net.toknsmith.login.http.StatusCode;

import java.util.Optional;

public class UnAuthorizedException extends ErrorResponseException {

    public UnAuthorizedException(Optional<TokenErrorResponse> errorResponse) {
        super(errorResponse, StatusCode.UNAUTHORIZED.getCode());
    }

    public UnAuthorizedException(TokenErrorResponse error) {
        super(Optional.of(error), StatusCode.UNAUTHORIZED.getCode());
    }
}
