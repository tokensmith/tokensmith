package org.rootservices.authorization.http.controller.resource.html.authorization;


import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import org.rootservices.authorization.http.controller.resource.html.authorization.helper.AuthorizationFactory;
import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AuthorizationResource extends Resource<TokenSession, WebSiteUser> {
    public static String URL = "^/authorization?.*";
    private static String RESPONSE_TYPE = "response_type";
    private static String SCOPE = "scope";

    private AuthorizationFactory authorizationFactory;

    @Autowired
    public AuthorizationResource(AuthorizationFactory authorizationFactory) {
        this.authorizationFactory = authorizationFactory;
    }

    public Response<TokenSession> get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        Resource<TokenSession, WebSiteUser> resource = authorizationFactory.makeResource(
                request.getQueryParams().get(SCOPE),
                request.getQueryParams().get(RESPONSE_TYPE)
        );
        return resource.get(request, response);
    }

    public Response<TokenSession> post(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        Resource<TokenSession, WebSiteUser> resource = authorizationFactory.makeResource(
                request.getQueryParams().get(SCOPE),
                request.getQueryParams().get(RESPONSE_TYPE)
        );
        return resource.post(request, response);
    }
}
