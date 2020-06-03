package net.tokensmith.authorization.http.presenter.profile;

import java.util.Optional;
import java.util.UUID;

public class Address {
    private UUID id;
    private UUID profileId;
    private String streetAddress;
    private Optional<String> streetAddress2;
    private String locality;
    private String region;
    private String postalCode;
    private String country;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Optional<String> getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(Optional<String> streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
