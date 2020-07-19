package net.toknsmith.login.exception.builder;

import net.toknsmith.login.endpoint.entity.response.openid.OpenIdToken;
import net.toknsmith.login.endpoint.entity.response.openid.TokenType;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import net.toknsmith.login.exception.IdTokenException;

import java.util.Optional;

public class IdTokenExceptionBuilder {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private TokenType tokenType;
    private User user;
    private String message;
    private Throwable cause;

    public IdTokenExceptionBuilder fromTokenEndpoint(OpenIdToken from) {
        this.accessToken = from.getAccessToken();
        this.refreshToken = from.getRefreshToken();
        this.expiresIn = from.getExpiresIn();
        this.tokenType = from.getTokenType();

        return this;
    }

    public IdTokenExceptionBuilder user(User user) {
        this.user = user;
        return this;
    }

    public IdTokenExceptionBuilder message(String message) {
        this.message = message;
        return this;
    }

    public IdTokenExceptionBuilder cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public IdTokenException build() {
        return new IdTokenException(
                message,
                cause,
                accessToken != null ? Optional.of(accessToken) : Optional.empty(),
                refreshToken != null ? Optional.of(refreshToken) : Optional.empty(),
                expiresIn != null ? Optional.of(expiresIn) : Optional.empty(),
                tokenType != null ? Optional.of(tokenType) : Optional.empty(),
                user
        );
    }
}
