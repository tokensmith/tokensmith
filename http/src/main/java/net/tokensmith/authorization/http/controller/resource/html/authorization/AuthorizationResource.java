package net.tokensmith.authorization.http.controller.resource.html.authorization;


import net.tokensmith.authorization.http.controller.resource.html.authorization.helper.AuthorizationFactory;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AuthorizationResource extends Resource<WebSiteSession, WebSiteUser> {
    public static String URL = "^/authorization?.*";
    private static String RESPONSE_TYPE = "response_type";
    private static String SCOPE = "scope";

    private AuthorizationFactory authorizationFactory;


    @Autowired
    public AuthorizationResource(AuthorizationFactory authorizationFactory) {
        this.authorizationFactory = authorizationFactory;
    }

    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        Resource<WebSiteSession, WebSiteUser> resource = authorizationFactory.makeResource(
                request.getQueryParams().get(SCOPE),
                request.getQueryParams().get(RESPONSE_TYPE)
        );

        response = resource.get(request, response);

        return response;
    }

    public Response<WebSiteSession> post(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        Resource<WebSiteSession, WebSiteUser> resource = authorizationFactory.makeResource(
                request.getQueryParams().get(SCOPE),
                request.getQueryParams().get(RESPONSE_TYPE)
        );

        response = resource.post(request, response);

        return response;
    }
}
