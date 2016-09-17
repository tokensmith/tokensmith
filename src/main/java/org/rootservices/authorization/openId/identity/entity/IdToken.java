package org.rootservices.authorization.openId.identity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rootservices.jwt.entity.jwt.Claims;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Created by tommackenzie on 10/20/15.
 * http://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
 */
public class IdToken extends Claims {

    // begin standard claims
    @JsonProperty(value="name")
    private Optional<String> fullName;

    @JsonProperty(value="family_name")
    private Optional<String> lastName;

    @JsonProperty(value="given_name")
    private Optional<String> firstName;

    @JsonProperty(value="middle_name")
    private Optional<String> middleName;

    @JsonProperty(value="nick_name")
    private Optional<String> nickName;

    @JsonProperty(value="preferred_username")
    private Optional<String> preferredUsername;

    @JsonProperty(value="profile")
    private Optional<URI> profile;

    @JsonProperty(value="picture")
    private Optional<URI> picture;

    @JsonProperty(value="website")
    private Optional<URI> website;

    @JsonProperty(value="gender")
    private Optional<String> gender;

    // [ISO8601‑2004] YYYY-MM-DD format
    @JsonProperty(value="birthdate")
    private Optional<OffsetDateTime> birthdate;

    // http://www.twinsun.com/tz/tz-link.htm
    @JsonProperty(value="zoneinfo")
    private Optional<String> zoneInfo;

    // example.. en-US or fr-CA
    @JsonProperty(value="locale")
    private Optional<String> locale;

    @JsonProperty(value="updated_at")
    private Optional<Long> updatedAt;

    @JsonProperty(value="email")
    private Optional<String> email;

    @JsonProperty(value="email_verified")
    private Optional<Boolean> emailVerified;

    @JsonProperty(value="address")
    private Optional<Address> address;

    @JsonProperty(value="phone_number")
    private Optional<String> phoneNumber;

    @JsonProperty(value="phone_number_verified")
    private Optional<Boolean> phoneNumberVerified;

    // end standard claims.

    @JsonProperty(value="auth_time")
    private long authenticationTime;

    @JsonProperty(value="nonce")
    private Optional<String> nonce;

    @JsonProperty(value="at_hash")
    private Optional<String> accessTokenHash;

    public IdToken() {
        super();
        fullName = Optional.empty();
        lastName = Optional.empty();
        firstName = Optional.empty();
        middleName = Optional.empty();
        nickName = Optional.empty();
        preferredUsername = Optional.empty();
        profile = Optional.empty();
        picture = Optional.empty();
        website = Optional.empty();
        gender = Optional.empty();
        birthdate = Optional.empty();
        zoneInfo = Optional.empty();
        locale = Optional.empty();
        updatedAt = Optional.empty();
        email = Optional.empty();
        emailVerified = Optional.empty();
        address = Optional.empty();
        phoneNumber = Optional.empty();
        phoneNumberVerified = Optional.empty();
        nonce = Optional.empty();
        accessTokenHash = Optional.empty();
    }

    public long getAuthenticationTime() {
        return authenticationTime;
    }

    public void setAuthenticationTime(long authenticationTime) {
        this.authenticationTime = authenticationTime;
    }

    public Optional<String> getFullName() {
        return fullName;
    }

    public void setFullName(Optional<String> fullName) {
        this.fullName = fullName;
    }

    public Optional<String> getLastName() {
        return lastName;
    }

    public void setLastName(Optional<String> lastName) {
        this.lastName = lastName;
    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(Optional<String> firstName) {
        this.firstName = firstName;
    }

    public Optional<String> getMiddleName() {
        return middleName;
    }

    public void setMiddleName(Optional<String> middleName) {
        this.middleName = middleName;
    }

    public Optional<String> getNickName() {
        return nickName;
    }

    public void setNickName(Optional<String> nickName) {
        this.nickName = nickName;
    }

    public Optional<String> getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(Optional<String> preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public Optional<URI> getProfile() {
        return profile;
    }

    public void setProfile(Optional<URI> profile) {
        this.profile = profile;
    }

    public Optional<URI> getPicture() {
        return picture;
    }

    public void setPicture(Optional<URI> picture) {
        this.picture = picture;
    }

    public Optional<URI> getWebsite() {
        return website;
    }

    public void setWebsite(Optional<URI> website) {
        this.website = website;
    }

    public Optional<String> getGender() {
        return gender;
    }

    public void setGender(Optional<String> gender) {
        this.gender = gender;
    }

    public Optional<OffsetDateTime> getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Optional<OffsetDateTime> birthdate) {
        this.birthdate = birthdate;
    }

    public Optional<String> getZoneInfo() {
        return zoneInfo;
    }

    public void setZoneInfo(Optional<String> zoneInfo) {
        this.zoneInfo = zoneInfo;
    }

    public Optional<String> getLocale() {
        return locale;
    }

    public void setLocale(Optional<String> locale) {
        this.locale = locale;
    }

    public Optional<Long> getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Optional<Long> updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public void setEmail(Optional<String> email) {
        this.email = email;
    }

    public Optional<Boolean> getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Optional<Boolean> emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Optional<Address> getAddress() {
        return address;
    }

    public void setAddress(Optional<Address> address) {
        this.address = address;
    }

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Optional<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Optional<Boolean> getPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public void setPhoneNumberVerified(Optional<Boolean> phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public Optional<String> getNonce() {
        return nonce;
    }

    public void setNonce(Optional<String> nonce) {
        this.nonce = nonce;
    }

    public Optional<String> getAccessTokenHash() {
        return accessTokenHash;
    }

    public void setAccessTokenHash(Optional<String> accessTokenHash) {
        this.accessTokenHash = accessTokenHash;
    }
}
