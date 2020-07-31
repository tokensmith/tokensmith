package net.tokensmith.authorization.http.controller.resource.api.publik;

import net.tokensmith.authorization.http.controller.resource.api.publik.model.Health;
import net.tokensmith.authorization.http.controller.security.APIUser;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.repository.repo.HealthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HealthResource extends RestResource<APIUser, Health> {
    public static String URL = "/api/public/v1/health";
    protected static Logger LOGGER = LoggerFactory.getLogger(HealthResource.class);
    private HealthRepository healthRepository;

    @Autowired
    public HealthResource(HealthRepository healthRepository) {
        this.healthRepository = healthRepository;
    }

    @Override
    public RestResponse<Health> get(RestRequest<APIUser, Health> request, RestResponse<Health> response) {
        Boolean ok = false;
        try {
            ok = healthRepository.isOk();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (Boolean.FALSE.equals(ok)) {
            notOk(response);
        } else {
            Health health = new Health(Health.Status.UP);
            response.setPayload(Optional.of(health));
            response.setStatusCode(StatusCode.OK);
        }
        return response;
    }

    public void notOk(RestResponse<Health> response) {
        Health health = new Health(Health.Status.DOWN);
        response.setPayload(Optional.of(health));
        response.setStatusCode(StatusCode.SERVER_ERROR);
    }
}
