package net.tokensmith.authorization.http.controller.resource.html.error;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.authorization.http.controller.security.TokenSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;

import java.util.Optional;

public class MediaTypeResource extends Resource<TokenSession, WebSiteUser> {
    private static Optional<String> JSP_PATH = Optional.of("/WEB-INF/jsp/415.jsp");

    @Override
    public Response<TokenSession> get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> put(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> delete(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> connect(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> options(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> trace(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> patch(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> head(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }
}

