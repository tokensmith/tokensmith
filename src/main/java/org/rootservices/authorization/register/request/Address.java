package org.rootservices.authorization.register.request;


import java.util.Optional;

public class Address {

    private String streetAddress1;
    private Optional<String> streetAddress2;
    private String locality;
    private String region;
    private String postalCode;
    private String country;

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
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
