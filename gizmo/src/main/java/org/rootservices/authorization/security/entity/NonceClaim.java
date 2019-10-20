package org.rootservices.authorization.security.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rootservices.jwt.entity.jwt.Claims;

public class NonceClaim extends Claims {

    @JsonProperty(value="n")
    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
