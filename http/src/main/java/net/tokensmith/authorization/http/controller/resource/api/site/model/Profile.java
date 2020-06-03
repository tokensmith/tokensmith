package net.tokensmith.authorization.http.controller.resource.api.site.model;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class Profile {
    @NotNull
    private UUID id;
    @NotNull
    private UUID resourceOwnerId;
    @Email(message = "Email should be valid")
    private String email;
    private Optional<String> name;
    private Optional<String> middleName;
    private Optional<String> nickName;
    private Optional<String> preferredUserName;
    private Optional<URI> profile;
    private Optional<URI> picture;
    private Optional<URI> website;
    private Optional<String> gender;
    private Optional<LocalDate> birthDate;
    private Optional<String> zoneInfo;
    private Optional<String> locale;
    private Optional<String> phoneNumber;

    private Name givenName;
    private Name familyName;

    public Profile() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getResourceOwnerId() {
        return resourceOwnerId;
    }

    public void setResourceOwnerId(UUID resourceOwnerId) {
        this.resourceOwnerId = resourceOwnerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
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

    public Optional<String> getPreferredUserName() {
        return preferredUserName;
    }

    public void setPreferredUserName(Optional<String> preferredUserName) {
        this.preferredUserName = preferredUserName;
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

    public Optional<LocalDate> getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Optional<LocalDate> birthDate) {
        this.birthDate = birthDate;
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

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Optional<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Name getGivenName() {
        return givenName;
    }

    public void setGivenName(Name givenName) {
        this.givenName = givenName;
    }

    public Name getFamilyName() {
        return familyName;
    }

    public void setFamilyName(Name familyName) {
        this.familyName = familyName;
    }
}
