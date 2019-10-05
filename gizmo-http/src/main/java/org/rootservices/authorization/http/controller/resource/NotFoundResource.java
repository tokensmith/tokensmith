package org.rootservices.authorization.http.controller.resource;


import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;


public class NotFoundResource extends Resource<TokenSession, WebSiteUser> {
    public static String URL = "/notFound";
    public Response get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response post(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response put(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response delete(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response connect(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response options(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response trace(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response patch(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> head(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }
}
