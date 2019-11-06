package net.tokensmith.repository.entity;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/16.
 */
public class RSAPrivateKey {
    private UUID id;
    private KeyUse use;
    private BigInteger modulus;
    private BigInteger publicExponent;
    private BigInteger privateExponent;
    private BigInteger primeP;
    private BigInteger primeQ;
    private BigInteger primeExponentP;
    private BigInteger primeExponentQ;
    private BigInteger crtCoefficient;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public KeyUse getUse() {
        return use;
    }

    public void setUse(KeyUse use) {
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

    public BigInteger getPrivateExponent() {
        return privateExponent;
    }

    public void setPrivateExponent(BigInteger privateExponent) {
        this.privateExponent = privateExponent;
    }

    public BigInteger getPrimeP() {
        return primeP;
    }

    public void setPrimeP(BigInteger primeP) {
        this.primeP = primeP;
    }

    public BigInteger getPrimeQ() {
        return primeQ;
    }

    public void setPrimeQ(BigInteger primeQ) {
        this.primeQ = primeQ;
    }

    public BigInteger getPrimeExponentP() {
        return primeExponentP;
    }

    public void setPrimeExponentP(BigInteger primeExponentP) {
        this.primeExponentP = primeExponentP;
    }

    public BigInteger getPrimeExponentQ() {
        return primeExponentQ;
    }

    public void setPrimeExponentQ(BigInteger primeExponentQ) {
        this.primeExponentQ = primeExponentQ;
    }

    public BigInteger getCrtCoefficient() {
        return crtCoefficient;
    }

    public void setCrtCoefficient(BigInteger crtCoefficient) {
        this.crtCoefficient = crtCoefficient;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
