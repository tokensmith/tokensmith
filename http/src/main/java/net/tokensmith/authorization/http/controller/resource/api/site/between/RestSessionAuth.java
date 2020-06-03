package net.tokensmith.authorization.http.controller.resource.api.site.between;


import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class RestSessionAuth implements RestBetween<WebSiteSession, WebSiteUser> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestSessionAuth.class);
    private HashToken hashToken;
    private ResourceOwnerRepository resourceOwnerRepo;


    @Autowired
    public RestSessionAuth(HashToken hashToken, ResourceOwnerRepository resourceOwnerRepo) {
        this.hashToken = hashToken;
        this.resourceOwnerRepo = resourceOwnerRepo;
    }

    @Override
    public void process(Method method, RestBtwnRequest<WebSiteSession, WebSiteUser> request, RestBtwnResponse response) throws HaltException {
        if(request.getSession().isPresent()) {
            ResourceOwner resourceOwner = null;
            try {
                String hashedToken = hashToken.run(request.getSession().get().getToken());
                resourceOwner = resourceOwnerRepo.getByLocalToken(hashedToken);
            } catch (RecordNotFoundException e) {
                LOGGER.debug(e.getMessage(), e);
                fail(request, response);
            }

            WebSiteUser user = new WebSiteUser.Builder()
                    .id(resourceOwner.getId())
                    .email(resourceOwner.getEmail())
                    .emailVerified(resourceOwner.isEmailVerified())
                    .createdAt(resourceOwner.getCreatedAt())
                    .build();

            LOGGER.debug("authenticated user id: {}", user.getId());
            request.setUser(Optional.of(user));
        } else {
            fail(request, response);
        }
    }

    protected void fail(RestBtwnRequest<WebSiteSession, WebSiteUser> request, RestBtwnResponse response) throws HaltException {
        LOGGER.debug("failed to authenticate user");
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        throw new HaltException("Must be authenticated.");
    }
}
