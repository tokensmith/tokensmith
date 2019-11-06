package net.tokensmith.authorization.http.controller.resource.html;


import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.authorization.http.controller.security.TokenSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;



public class NotFoundResource extends Resource<TokenSession, WebSiteUser> {
    public static String URL = "(.*)";
    public Response<TokenSession> get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> post(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> put(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> delete(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> connect(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> options(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> trace(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> patch(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response<TokenSession> head(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }
}
