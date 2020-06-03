package net.tokensmith.authorization.http.controller.resource.api.site;

import net.tokensmith.authorization.http.controller.resource.api.site.model.Profile;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.service.ProfileService;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class RestProfileResource extends RestResource<WebSiteUser, Profile> {
    public static String URL = "/api/site/v1/profile(?!/)(.*)";

    private ProfileService profileService;

    @Autowired
    public RestProfileResource(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public RestResponse<Profile> put(RestRequest<WebSiteUser, Profile> request, RestResponse<Profile> response) {
        Optional<Profile> payload = Optional.empty();
        if (request.getUser().isPresent() && request.getPayload().isPresent()) {
            Profile profile = profileService.updateProfile(
                    request.getUser().get().getId(),
                    request.getPayload().get());

            payload = Optional.of(profile);
        }
        response.setPayload(payload);
        response.setStatusCode(StatusCode.OK);
        return response;
    }
}
