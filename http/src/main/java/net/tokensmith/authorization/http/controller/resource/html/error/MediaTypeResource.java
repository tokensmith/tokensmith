package net.tokensmith.authorization.http.controller.resource.html.error;

import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class MediaTypeResource extends Resource<WebSiteSession, WebSiteUser> {
    private static Optional<String> JSP_PATH = Optional.of("/WEB-INF/jsp/415.jsp");
    private String globalCssPath;

    @Autowired
    public MediaTypeResource(String globalCssPath) {
        this.globalCssPath = globalCssPath;
    }

    @Override
    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> post(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> put(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> delete(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> connect(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> options(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> trace(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> patch(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> head(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    protected AssetPresenter makeAssetPresenter() {
        return new AssetPresenter(globalCssPath);
    }
}

