package org.rootservices.authorization.oauth2.grant.token.entity;

import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.persistence.entity.Token;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/11/16.
 */
public class TokenGraph {
    private Token token;
    private UUID refreshTokenId;
    private String plainTextAccessToken;
    private String plainTextRefreshToken;
    private Extension extension;

    public TokenGraph() {}

    public TokenGraph(Token token, UUID refreshTokenId, String plainTextAccessToken, String plainTextRefreshToken, Extension extension) {
        this.token = token;
        this.refreshTokenId = refreshTokenId;
        this.plainTextAccessToken = plainTextAccessToken;
        this.plainTextRefreshToken = plainTextRefreshToken;
        this.extension = extension;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public UUID getRefreshTokenId() {
        return refreshTokenId;
    }

    public void setRefreshTokenId(UUID refreshTokenId) {
        this.refreshTokenId = refreshTokenId;
    }

    public String getPlainTextAccessToken() {
        return plainTextAccessToken;
    }

    public void setPlainTextAccessToken(String plainTextAccessToken) {
        this.plainTextAccessToken = plainTextAccessToken;
    }

    public String getPlainTextRefreshToken() {
        return plainTextRefreshToken;
    }

    public void setPlainTextRefreshToken(String plainTextRefreshToken) {
        this.plainTextRefreshToken = plainTextRefreshToken;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }
}
