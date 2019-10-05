package org.rootservices.authorization.http.controller.resource.html.error;

import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;

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

