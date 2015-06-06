package org.rootservices.authorization.grant.code.protocol.token;

import java.util.Optional;

/**
 * Created by tommackenzie on 6/4/15.
 */
public class TokenRequest {
    private String clientUUID;
    private String clientPassword;
    private String grantType;
    private String code;
    private String redirectUri;

    public TokenRequest(){}

    public String getClientUUID() {
        return clientUUID;
    }

    public void setClientUUID(String clientUUID) {
        this.clientUUID = clientUUID;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String authorizationCode) {
        this.code = code;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
