package org.rootservices.authorization.openId.jwk.entity;

import org.rootservices.authorization.persistence.entity.PrivateKeyUse;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/1/17.
 *
 */
public class RSAPublicKey {
    private UUID id;
    private PrivateKeyUse use;
    private BigInteger modulus; // n
    private BigInteger publicExponent; // e

    public RSAPublicKey(UUID id, PrivateKeyUse use, BigInteger modulus, BigInteger publicExponent) {
        this.id = id;
        this.use = use;
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PrivateKeyUse getUse() {
        return use;
    }

    public void setUse(PrivateKeyUse use) {
        this.use = use;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public void setModulus(BigInteger modulus) {
        this.modulus = modulus;
    }

    public BigInteger getPublicExponent() {
        return publicExponent;
    }

    public void setPublicExponent(BigInteger publicExponent) {
        this.publicExponent = publicExponent;
    }
}
