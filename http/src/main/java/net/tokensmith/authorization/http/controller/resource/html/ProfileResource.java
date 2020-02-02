package net.tokensmith.authorization.http.controller.resource.html;

import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.authorization.http.presenter.profile.ProfilePresenter;
import net.tokensmith.authorization.http.presenter.translator.ProfilePresenterTranslator;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProfileResource extends Resource<WebSiteSession, WebSiteUser> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileResource.class);
    public static String URL = "/profile(.*)";
    private static String JSP_FORM_PATH = "/WEB-INF/jsp/profile/profile.jsp";

    private String globalCssPath;
    private ProfilePresenterTranslator profilePresenterTranslator;
    private ResourceOwnerRepository resourceOwnerRepository;

    @Autowired
    public ProfileResource(String globalCssPath, ProfilePresenterTranslator profilePresenterTranslator, ResourceOwnerRepository resourceOwnerRepository) {
        this.globalCssPath = globalCssPath;
        this.profilePresenterTranslator = profilePresenterTranslator;
        this.resourceOwnerRepository = resourceOwnerRepository;
    }

    @Override
    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {

        ResourceOwner resourceOwner = null;
        try {
            resourceOwner = resourceOwnerRepository.getByIdWithProfile(request.getUser().get().getId());
        } catch (RecordNotFoundException e) {
            // unlikely to occur - most likely if a user is deactivated mid request.
            LOGGER.error(e.getMessage(), e);
            unAuthorizedResponse(response);
            return response;
        }

        ProfilePresenter presenter = profilePresenterTranslator.to(resourceOwner);
        presenter.setEncodedCsrfToken(request.getCsrfChallenge().get());
        presenter.setGlobalCssPath(globalCssPath);

        response.setStatusCode(StatusCode.OK);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(JSP_FORM_PATH));

        return response;
    }

    protected void unAuthorizedResponse(Response<WebSiteSession> response) {
        AssetPresenter presenter = new AssetPresenter();
        presenter.setGlobalCssPath(globalCssPath);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of("/WEB-INF/jsp/401.jsp"));
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        response.setSession(Optional.empty());
    }
}
