package net.tokensmith.authorization.http.controller.resource.html;


import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.authorization.http.controller.security.TokenSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class NotFoundResource extends Resource<TokenSession, WebSiteUser> {
    public static String URL = "(.*)";
    private static Optional<String> JSP_PATH = Optional.of("/WEB-INF/jsp/404.jsp");
    private String globalCssPath;

    @Autowired
    public NotFoundResource(String globalCssPath) {
        this.globalCssPath = globalCssPath;
    }

    public Response<TokenSession> get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    public Response<TokenSession> post(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    public Response<TokenSession> put(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    public Response<TokenSession> delete(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    public Response<TokenSession> connect(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    public Response<TokenSession> options(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    public Response<TokenSession> trace(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    public Response<TokenSession> patch(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    public Response<TokenSession> head(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        prepareResponse(response);
        return response;
    }

    protected void prepareResponse(Response<TokenSession> response) {
        AssetPresenter presenter = makeAssetPresenter();
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.NOT_FOUND);
        response.setTemplate(JSP_PATH);
    }

    protected AssetPresenter makeAssetPresenter() {
        return new AssetPresenter(globalCssPath);
    }
}
