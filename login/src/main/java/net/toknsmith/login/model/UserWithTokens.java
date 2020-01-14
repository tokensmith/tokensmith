package net.toknsmith.login.model;

import net.toknsmith.login.endpoint.entity.response.openid.TokenType;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;

/**
 * Returned to clients of Login interface.
 */
public class UserWithTokens {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private TokenType tokenType;
    private User user;

    public UserWithTokens(String accessToken, String refreshToken, Long expiresIn, TokenType tokenType, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return  new StringBuilder()
        .append("UserWithTokens { ")
                .append("accessToken='").append(accessToken.substring(0, 4)).append("****\'")
                .append(", refreshToken='").append(refreshToken.substring(0, 4)).append("****\'")
                .append(", expiresIn=").append(expiresIn)
                .append(", tokenType=").append(tokenType)
                .append(", user=").append(user)
                .append('}').toString();
    }
}
