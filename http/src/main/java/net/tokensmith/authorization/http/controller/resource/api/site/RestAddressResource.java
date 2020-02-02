package net.tokensmith.authorization.http.controller.resource.api.site;

import net.tokensmith.authorization.http.controller.resource.api.site.model.Address;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Profile;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.service.ProfileService;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.router.entity.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
public class RestAddressResource extends RestResource<WebSiteUser, Address> {
    public static String URL = "/api/site/v1/profile/(.*)/address(/?)(?<id>.*)";

    private ProfileService profileService;

    @Autowired
    public RestAddressResource(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public RestResponse<Address> post(RestRequest<WebSiteUser, Address> request, RestResponse<Address> response) {
        Optional<Address> payload = Optional.empty();
        if (request.getPayload().isPresent()) {
            Address address = profileService.createAddress(request.getPayload().get());
            payload = Optional.of(address);
        }
        response.setPayload(payload);
        response.setStatusCode(StatusCode.CREATED);
        return response;
    }

    @Override
    public RestResponse<Address> put(RestRequest<WebSiteUser, Address> request, RestResponse<Address> response) {
        Optional<Address> payload = Optional.empty();
        if (request.getUser().isPresent() && request.getPayload().isPresent()) {
            Address address = profileService.updateAddress(
                    request.getUser().get().getId(),
                    request.getPayload().get());
            payload = Optional.of(address);
        }
        response.setPayload(payload);
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public RestResponse<Address> delete(RestRequest<WebSiteUser, Address> request, RestResponse<Address> response) {

        if (request.getUser().isPresent()) {
            UUID id = UUID.fromString(request.getMatcher().get().group("id"));
            profileService.deleteAddress(
                id,
                request.getUser().get().getId()
            );
        }
        response.setStatusCode(StatusCode.OK);
        return response;
    }
}
