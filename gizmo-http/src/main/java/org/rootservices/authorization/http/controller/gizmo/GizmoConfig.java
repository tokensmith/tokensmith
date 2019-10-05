package org.rootservices.authorization.http.controller.gizmo;

import org.eclipse.jetty.server.Authentication;
import org.rootservices.authorization.http.controller.resource.api.HealthResource;
import org.rootservices.authorization.http.controller.resource.api.model.Health;
import org.rootservices.authorization.http.controller.resource.html.error.MediaTypeResource;
import org.rootservices.authorization.http.controller.resource.html.error.ServerErrorResource;
import org.rootservices.authorization.http.controller.security.APIUser;
import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.gateway.builder.*;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.entity.rest.RestGroup;
import org.rootservices.otter.gateway.entity.rest.RestTarget;
import org.rootservices.otter.router.entity.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GizmoConfig implements Configure {
    public static final String WEB_SITE_GROUP = "WebSite";
    public static final String API_GROUP_V1 = "API_V1";

    @Override
    public Shape shape() {

        // TODO: add vaulting
        SymmetricKey csrfKey = new SymmetricKey(
                Optional.of("key-1"),
                "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
                Use.SIGNATURE
        );

        SymmetricKey encKey = new SymmetricKey(
                Optional.of("key-2"),
                "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
                Use.ENCRYPTION
        );

        return new ShapeBuilder()
                .secure(false)
                .encKey(encKey)
                .signkey(csrfKey)
                .build();
    }

    @Override
    public List<Group<? extends DefaultSession, ? extends DefaultUser>> groups() {
        List<Group<? extends DefaultSession, ? extends DefaultUser>> groups = new ArrayList<>();

        var serverErrorResource = new ServerErrorResource();

        ErrorTarget<TokenSession, WebSiteUser> mediaType = new ErrorTargetBuilder<TokenSession, WebSiteUser>()
                .resource(new MediaTypeResource())
                .build();

        // TODO: does this need authOpt or authRequired?
        Group<TokenSession, WebSiteUser> webSiteGroup = new GroupBuilder<TokenSession, WebSiteUser>()
                .name(WEB_SITE_GROUP)
                .sessionClazz(TokenSession.class)
                .onError(StatusCode.SERVER_ERROR, serverErrorResource)
                .onDispatchError(StatusCode.UNSUPPORTED_MEDIA_TYPE, mediaType)
                .build();

        groups.add(webSiteGroup);

        return groups;
    }

    @Override
    public List<RestGroup<? extends DefaultUser>> restGroups() {
        List<RestGroup<? extends DefaultUser>> restGroups = new ArrayList<>();

        // TODO: does this need authOpt or authRequired?
        // uses default bad request handling.
        RestGroup<APIUser> apiGroupV2 = new RestGroupBuilder<APIUser>()
                .name(API_GROUP_V1)
                .build();

        restGroups.add(apiGroupV2);

        return restGroups;
    }

    @Override
    public void routes(Gateway gateway) {
        webSiteRoutes(gateway);
        apiRoutes(gateway);
    }

    protected void webSiteRoutes(Gateway gateway) {

        // include not founds
    }

    protected void apiRoutes(Gateway gateway) {

        MimeType json = new MimeTypeBuilder().json().build();

        RestTarget<APIUser, Health> health = new RestTargetBuilder<APIUser, Health>()
                .groupName(API_GROUP_V1)
                .method(Method.GET)
                .restResource(new HealthResource())
                .regex(HealthResource.URL)
                .authenticate()
                .contentType(json)
                .payload(Health.class)
                .build();

        gateway.add(health);

        // include not founds
    }

}
