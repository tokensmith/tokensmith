package net.toknsmith.login.exception.http.openid;

import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;
import net.toknsmith.login.http.StatusCode;

import java.util.Optional;


public class ServerError extends ErrorResponseException {
    public ServerError(Optional<TokenErrorResponse> errorResponse) {
        super(errorResponse, StatusCode.SERVER_ERROR.getCode());
    }

    public ServerError(TokenErrorResponse error) {
        super(Optional.of(error), StatusCode.SERVER_ERROR.getCode());
    }
}
