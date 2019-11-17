package net.tokensmith.repository.entity;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents the data stored in the database.
 *
 * The key fields are stored encrypted
 */
public class RSAPrivateKeyBytes {
    private UUID id;
    private KeyUse use;
    private byte[] modulus;
    private byte[] publicExponent;
    private byte[] privateExponent;
    private byte[] primeP;
    private byte[] primeQ;
    private byte[] primeExponentP;
    private byte[] primeExponentQ;
    private byte[] crtCoefficient;
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

    public byte[] getModulus() {
        return modulus;
    }

    public void setModulus(byte[] modulus) {
        this.modulus = modulus;
    }

    public byte[] getPublicExponent() {
        return publicExponent;
    }

    public void setPublicExponent(byte[] publicExponent) {
        this.publicExponent = publicExponent;
    }

    public byte[] getPrivateExponent() {
        return privateExponent;
    }

    public void setPrivateExponent(byte[] privateExponent) {
        this.privateExponent = privateExponent;
    }

    public byte[] getPrimeP() {
        return primeP;
    }

    public void setPrimeP(byte[] primeP) {
        this.primeP = primeP;
    }

    public byte[] getPrimeQ() {
        return primeQ;
    }

    public void setPrimeQ(byte[] primeQ) {
        this.primeQ = primeQ;
    }

    public byte[] getPrimeExponentP() {
        return primeExponentP;
    }

    public void setPrimeExponentP(byte[] primeExponentP) {
        this.primeExponentP = primeExponentP;
    }

    public byte[] getPrimeExponentQ() {
        return primeExponentQ;
    }

    public void setPrimeExponentQ(byte[] primeExponentQ) {
        this.primeExponentQ = primeExponentQ;
    }

    public byte[] getCrtCoefficient() {
        return crtCoefficient;
    }

    public void setCrtCoefficient(byte[] crtCoefficient) {
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
