package net.tokensmith.authorization.http.controller.resource.html.authorization;


import net.tokensmith.authorization.http.controller.resource.html.authorization.claim.RedirectClaim;
import net.tokensmith.jwt.builder.compact.UnsecureCompactBuilder;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.authorization.http.controller.resource.html.authorization.helper.AuthorizationFactory;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;


@Component
public class AuthorizationResource extends Resource<WebSiteSession, WebSiteUser> {
    public static String URL = "^/authorization?.*";
    private static String RESPONSE_TYPE = "response_type";
    private static String SCOPE = "scope";

    private AuthorizationFactory authorizationFactory;
    private UnsecureCompactBuilder jwtBuilder;

    @Autowired
    public AuthorizationResource(AuthorizationFactory authorizationFactory, UnsecureCompactBuilder jwtBuilder) {
        this.authorizationFactory = authorizationFactory;
        this.jwtBuilder = jwtBuilder;
    }

    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        Resource<WebSiteSession, WebSiteUser> resource = authorizationFactory.makeResource(
                request.getQueryParams().get(SCOPE),
                request.getQueryParams().get(RESPONSE_TYPE)
        );

        response = resource.get(request, response);

        if (StatusCode.OK.equals(response.getStatusCode())) {
            Cookie redirectCookie = redirectCookie(request.getPathWithParams());
            response.getCookies().put(redirectCookie.getName(), redirectCookie);
        } else {
            response.getCookies().remove("redirect");
        }

        return response;
    }

    public Response<WebSiteSession> post(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        Resource<WebSiteSession, WebSiteUser> resource = authorizationFactory.makeResource(
                request.getQueryParams().get(SCOPE),
                request.getQueryParams().get(RESPONSE_TYPE)
        );

        response = resource.post(request, response);

        if(StatusCode.MOVED_TEMPORARILY.equals(response.getStatusCode()) ||
           StatusCode.NOT_FOUND.equals(response.getStatusCode())) {
            response.getCookies().remove("redirect");
        }

        return response;
    }

    protected Cookie redirectCookie(String redirectPath) {
        // use a jwt as the value just in case there's a future need to sign, add more data, etc.
        RedirectClaim redirectClaim = new RedirectClaim(redirectPath);
        redirectClaim.setIssuedAt(Optional.of(Instant.now().toEpochMilli()));
        var cookieValue = jwtBuilder.claims(redirectClaim).build();

        return new Cookie.Builder()
                .name("redirect")
                .value(new String(cookieValue.toByteArray(), StandardCharsets.UTF_8))
                .httpOnly(true)
                .build();
    }
}
