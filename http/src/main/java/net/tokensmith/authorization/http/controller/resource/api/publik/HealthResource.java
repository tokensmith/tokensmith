package net.tokensmith.authorization.http.controller.resource.api.publik;

import net.tokensmith.authorization.http.controller.resource.api.publik.model.Health;
import net.tokensmith.authorization.http.controller.security.APIUser;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.repository.repo.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HealthResource extends RestResource<APIUser, Health> {
    public static String URL = "/api/public/v1/health";
    protected static Logger LOGGER = LoggerFactory.getLogger(HealthResource.class);
    private ConfigurationRepository configurationRepository;

    @Autowired
    public HealthResource(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public RestResponse<Health> get(RestRequest<APIUser, Health> request, RestResponse<Health> response) {
        Health health = new Health(Health.Status.UP);
        response.setPayload(Optional.of(health));
        response.setStatusCode(StatusCode.OK);

        try {
            configurationRepository.get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            health.setStatus(Health.Status.DOWN);
            response.setStatusCode(StatusCode.SERVER_ERROR);
        }

        return response;
    }
}
