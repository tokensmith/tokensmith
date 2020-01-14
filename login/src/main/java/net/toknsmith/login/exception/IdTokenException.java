package net.toknsmith.login.exception;

import net.toknsmith.login.endpoint.entity.response.openid.TokenType;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;

import java.util.Optional;


/**
 * Thrown whenever there was an issue with id_token jwt.
 * cause will most likely be a JwtException
 *
 * The ivars exist in case the caller wants to proceed even if there are jwt problems.
 */
public class IdTokenException extends Exception {
    private Optional<String> accessToken;
    private Optional<String> refreshToken;
    private Optional<Long> expiresIn;
    private Optional<TokenType> tokenType;
    private User user;

    public IdTokenException(String message, Throwable cause, Optional<String> accessToken, Optional<String> refreshToken, Optional<Long> expiresIn, Optional<TokenType> tokenType, User user) {
        super(message, cause);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.user = user;
    }

    public Optional<String> getAccessToken() {
        return accessToken;
    }

    public Optional<String> getRefreshToken() {
        return refreshToken;
    }

    public Optional<Long> getExpiresIn() {
        return expiresIn;
    }

    public Optional<TokenType> getTokenType() {
        return tokenType;
    }

    public User getUser() {
        return user;
    }
}
