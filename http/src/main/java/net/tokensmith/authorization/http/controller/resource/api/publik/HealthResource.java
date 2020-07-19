package net.tokensmith.authorization.http.controller.resource.api.publik;

import net.tokensmith.authorization.http.controller.resource.api.publik.model.Health;
import net.tokensmith.authorization.http.controller.security.APIUser;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;

import java.util.Optional;

public class HealthResource extends RestResource<APIUser, Health> {
    public static String URL = "/api/public/v1/health";

    @Override
    public RestResponse<Health> get(RestRequest<APIUser, Health> request, RestResponse<Health> response) {
        Health health = new Health(Health.Status.UP);
        response.setPayload(Optional.of(health));
        response.setStatusCode(StatusCode.OK);
        return response;
    }
}
