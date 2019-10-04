package org.rootservices.authorization.register.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.rootservices.otter.translatable.Translatable;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

public class UserInfo implements Translatable {

    private String email;
    private String password;

    private Optional<String> name;
    private Optional<String> familyName;
    private Optional<String> givenName;
    private Optional<String> middleName;
    private Optional<String> nickName;
    private Optional<String> preferredUserName;
    private Optional<URI> profile;
    private Optional<URI> picture;
    private Optional<URI> website;
    private Optional<String> gender;
    @JsonProperty(value="birthdate")
    private Optional<LocalDate> birthDate;
    @JsonProperty(value="zoneinfo")
    private Optional<String> zoneInfo;
    private Optional<String> locale;
    private Optional<String> phoneNumber;
    private Optional<Address> address;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
    }

    public Optional<String> getFamilyName() {
        return familyName;
    }

    public void setFamilyName(Optional<String> familyName) {
        this.familyName = familyName;
    }

    public Optional<String> getGivenName() {
        return givenName;
    }

    public void setGivenName(Optional<String> givenName) {
        this.givenName = givenName;
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

    public Optional<Address> getAddress() {
        return address;
    }

    public void setAddress(Optional<Address> address) {
        this.address = address;
    }
}
