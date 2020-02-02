package net.tokensmith.authorization.http.service.translator;

import net.tokensmith.repository.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressTranslator {

    public Address toEntity(net.tokensmith.authorization.http.controller.resource.api.site.model.Address from) {
        Address to = new Address();
        to.setId(from.getId());
        to.setProfileId(from.getProfileId());
        to.setStreetAddress(from.getStreetAddress());
        to.setStreetAddress2(from.getStreetAddress2());
        to.setLocality(from.getLocality());
        to.setRegion(from.getRegion());
        to.setPostalCode(from.getPostalCode());
        to.setCountry(from.getCountry());
        return to;
    }

    public net.tokensmith.authorization.http.controller.resource.api.site.model.Address toModel(Address from) {
        var to = new net.tokensmith.authorization.http.controller.resource.api.site.model.Address();
        to.setId(from.getId());
        to.setProfileId(from.getProfileId());
        to.setStreetAddress(from.getStreetAddress());
        to.setStreetAddress2(from.getStreetAddress2());
        to.setLocality(from.getLocality());
        to.setRegion(from.getRegion());
        to.setPostalCode(from.getPostalCode());
        to.setCountry(from.getCountry());
        return to;
    }
}
