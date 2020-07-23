package net.tokensmith.authorization.http.controller.resource.html.between;


import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.Between;
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
public class WebSiteAuthRequired implements Between<WebSiteSession, WebSiteUser> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSiteAuthRequired.class);
    private static String UNAUTHORIZED_JSP_PATH = "/WEB-INF/jsp/403.jsp";
    private HashToken hashToken;
    private ResourceOwnerRepository resourceOwnerRepo;
    private String globalCssPath;

    @Autowired
    public WebSiteAuthRequired(HashToken hashToken, ResourceOwnerRepository resourceOwnerRepo, String globalCssPath) {
        this.hashToken = hashToken;
        this.resourceOwnerRepo = resourceOwnerRepo;
        this.globalCssPath = globalCssPath;
    }

    @Override
    public void process(Method method, Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) throws HaltException {
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

    protected void fail(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) throws HaltException {
        LOGGER.debug("failed to authenticate user");
        AssetPresenter presenter = new AssetPresenter();
        presenter.setGlobalCssPath(globalCssPath);
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        response.setTemplate(Optional.of(UNAUTHORIZED_JSP_PATH));
        throw new HaltException("Must be authenticated.");
    }
}
