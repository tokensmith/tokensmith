package org.rootservices.authorization.http.controller.resource.api;

import org.rootservices.authorization.http.controller.resource.api.model.Health;
import org.rootservices.authorization.http.controller.security.APIUser;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;

import java.util.Optional;

public class HealthResource extends RestResource<APIUser, Health> {
    public static String URL = "/api/v1/health";

    @Override
    public RestResponse<Health> get(RestRequest<APIUser, Health> request, RestResponse<Health> response) {
        Health health = new Health(Health.Status.UP);
        response.setPayload(Optional.of(health));
        response.setStatusCode(StatusCode.OK);
        return response;
    }
}
