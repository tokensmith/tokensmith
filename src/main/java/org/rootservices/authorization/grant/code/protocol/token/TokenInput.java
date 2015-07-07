package org.rootservices.authorization.grant.code.protocol.token;

import java.io.BufferedReader;
import java.util.Optional;

/**
 * Created by tommackenzie on 6/4/15.
 */
public class TokenInput {
    private String clientUUID;
    private String clientPassword;
    private BufferedReader payload;

    public TokenInput(){}

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

    public BufferedReader getPayload() {
        return payload;
    }

    public void setPayload(BufferedReader payload) {
        this.payload = payload;
    }
}
