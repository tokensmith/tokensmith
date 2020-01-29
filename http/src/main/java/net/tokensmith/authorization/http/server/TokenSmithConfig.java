package net.tokensmith.authorization.http.server;

import net.tokensmith.otter.controller.builder.MimeTypeBuilder;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.controller.error.rest.NotFoundRestResource;
import net.tokensmith.otter.gateway.Configure;
import net.tokensmith.otter.gateway.Gateway;
import net.tokensmith.otter.gateway.builder.*;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.entity.Target;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.gateway.entity.rest.RestTarget;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.authorization.http.config.HttpAppConfig;
import net.tokensmith.authorization.http.controller.resource.api.*;
import net.tokensmith.authorization.http.controller.resource.api.model.Health;
import net.tokensmith.authorization.http.controller.resource.html.ForgotPasswordResource;
import net.tokensmith.authorization.http.controller.resource.html.NotFoundResource;
import net.tokensmith.authorization.http.controller.resource.html.RegisterResource;
import net.tokensmith.authorization.http.controller.resource.html.UpdatePasswordResource;
import net.tokensmith.authorization.http.controller.resource.html.authorization.AuthorizationResource;
import net.tokensmith.authorization.http.controller.resource.html.authorization.welcome.WelcomeResource;
import net.tokensmith.authorization.http.controller.resource.html.error.MediaTypeResource;
import net.tokensmith.authorization.http.controller.resource.html.error.ServerErrorResource;
import net.tokensmith.authorization.http.controller.security.APIUser;
import net.tokensmith.authorization.http.controller.security.TokenSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.openId.jwk.entity.RSAPublicKey;
import net.tokensmith.authorization.register.request.UserInfo;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TokenSmithConfig implements Configure {
    public static final String WEB_SITE_GROUP = "WebSite";
    public static final String API_GROUP_V1 = "API_V1";
    public static MimeType JSON = new MimeTypeBuilder().json().build();

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
        ApplicationContext context = new AnnotationConfigApplicationContext(HttpAppConfig.class);
        webSiteRoutes(gateway, context);
        apiRoutes(gateway, context);
    }

    protected void webSiteRoutes(Gateway gateway, ApplicationContext context) {
        AuthorizationResource authorizationResource = context.getBean(AuthorizationResource.class);
        Target<TokenSession, WebSiteUser> authorizationTarget = new TargetBuilder<TokenSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .resource(authorizationResource)
                .regex(AuthorizationResource.URL)
                .build();

        gateway.add(authorizationTarget);

        RegisterResource registerResource = context.getBean(RegisterResource.class);
        Target<TokenSession, WebSiteUser> registerTarget = new TargetBuilder<TokenSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .resource(registerResource)
                .regex(RegisterResource.URL)
                .build();

        gateway.add(registerTarget);

        WelcomeResource welcomeResource = context.getBean(WelcomeResource.class);
        Target<TokenSession, WebSiteUser> welcomeTarget = new TargetBuilder<TokenSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .method(Method.GET)
                .resource(welcomeResource)
                .regex(WelcomeResource.URL)
                .build();

        gateway.add(welcomeTarget);

        ForgotPasswordResource forgotPasswordResource = context.getBean(ForgotPasswordResource.class);
        Target<TokenSession, WebSiteUser> forgotPasswordTarget = new TargetBuilder<TokenSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .resource(forgotPasswordResource)
                .regex(ForgotPasswordResource.URL)
                .build();

        gateway.add(forgotPasswordTarget);

        UpdatePasswordResource updatePasswordResource = context.getBean(UpdatePasswordResource.class);
        Target<TokenSession, WebSiteUser> updatePasswordTarget = new TargetBuilder<TokenSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .resource(updatePasswordResource)
                .regex(UpdatePasswordResource.URL)
                .build();

        gateway.add(updatePasswordTarget);

        // below are apis but are not json.
        TokenResource tokenResource = context.getBean(TokenResource.class);
        Target<TokenSession, APIUser> tokenTarget = new TargetBuilder<TokenSession, APIUser>()
                .groupName(WEB_SITE_GROUP)
                .method(Method.POST)
                .resource(tokenResource)
                .regex(TokenResource.URL)
                .build();

        gateway.add(tokenTarget);

        Target<TokenSession, WebSiteUser> notFoundTarget = new TargetBuilder<TokenSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .method(Method.GET)
                .method(Method.POST)
                .resource(new NotFoundResource())
                .regex(NotFoundResource.URL)
                .build();

        gateway.notFound(notFoundTarget);

    }

    protected void apiRoutes(Gateway gateway, ApplicationContext context) {

        RestTarget<APIUser, Health> healthTarget = new RestTargetBuilder<APIUser, Health>()
                .groupName(API_GROUP_V1)
                .method(Method.GET)
                .restResource(new HealthResource())
                .regex(HealthResource.URL)
                .contentType(JSON)
                .payload(Health.class)
                .build();

        gateway.add(healthTarget);

        UserInfoResource userInfoResource = context.getBean(UserInfoResource.class);
        RestTarget<APIUser, UserInfo> userInfoTarget = new RestTargetBuilder<APIUser, UserInfo>()
                .groupName(API_GROUP_V1)
                .method(Method.GET)
                .method(Method.POST)
                .restResource(userInfoResource)
                .regex(UserInfoResource.URL)
                .payload(UserInfo.class)
                .contentType(JSON)
                .build();

        gateway.add(userInfoTarget);

        RSAPublicKeyResource rsaPublicKeyResource = context.getBean(RSAPublicKeyResource.class);
        RestTarget<APIUser, RSAPublicKey> rsaPublicKeyTarget = new RestTargetBuilder<APIUser, RSAPublicKey>()
                .groupName(API_GROUP_V1)
                .method(Method.GET)
                .restResource(rsaPublicKeyResource)
                .regex(RSAPublicKeyResource.URL)
                .contentType(JSON)
                .payload(RSAPublicKey.class)
                .build();

        gateway.add(rsaPublicKeyTarget);

        RSAPublicKeysResource rsaPublicKeysResource = context.getBean(RSAPublicKeysResource.class);

        RestTarget<APIUser, RSAPublicKey[]> rsaPublicKeysTarget = new RestTargetBuilder<APIUser, RSAPublicKey[]>()
                .groupName(API_GROUP_V1)
                .method(Method.GET)
                .restResource(rsaPublicKeysResource)
                .regex(RSAPublicKeysResource.URL)
                .contentType(JSON)
                .payload(RSAPublicKey[].class)
                .build();

        gateway.add(rsaPublicKeysTarget);

        var restNotFoundResource = new NotFoundRestResource<APIUser>();
        RestTarget<APIUser, ClientError> notFoundTarget = new RestTargetBuilder<APIUser, ClientError>()
                .groupName(API_GROUP_V1)
                .crud()
                .restResource(restNotFoundResource)
                .regex("/api/v1/(.*)")
                .payload(ClientError.class)
                .build();

        gateway.notFound(notFoundTarget);
    }

}
