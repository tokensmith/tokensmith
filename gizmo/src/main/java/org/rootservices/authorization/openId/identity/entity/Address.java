package org.rootservices.authorization.openId.identity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by tommackenzie on 10/23/15.
 *
 * http://openid.net/specs/openid-connect-core-1_0.html#AddressClaim
 */
public class Address {
    @JsonProperty(value="formatted")
    private String formatted;

    @JsonProperty(value="street_address")
    private String streetAddress;

    @JsonProperty(value="locality")
    private String locality;

    @JsonProperty(value="region")
    private String region;

    @JsonProperty(value="postal_code")
    private String postalCode;

    @JsonProperty(value="country")
    private String country;

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
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
