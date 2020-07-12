package net.tokensmith.authorization.http.server;

import net.tokensmith.authorization.http.config.props.HttpProperties;
import net.tokensmith.authorization.http.controller.resource.api.publik.HealthResource;
import net.tokensmith.authorization.http.controller.resource.api.publik.RSAPublicKeyResource;
import net.tokensmith.authorization.http.controller.resource.api.publik.RSAPublicKeysResource;
import net.tokensmith.authorization.http.controller.resource.api.publik.TokenResource;
import net.tokensmith.authorization.http.controller.resource.api.publik.UserInfoResource;
import net.tokensmith.authorization.http.controller.resource.api.site.RestAddressResource;
import net.tokensmith.authorization.http.controller.resource.api.site.RestProfileResource;
import net.tokensmith.authorization.http.controller.resource.api.site.between.RestSessionAuth;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Address;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Profile;
import net.tokensmith.authorization.http.controller.resource.html.ProfileResource;
import net.tokensmith.authorization.http.controller.resource.html.between.CSPBetween;
import net.tokensmith.authorization.http.controller.resource.html.between.WebSiteAuthRequired;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.builder.MimeTypeBuilder;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.error.rest.NotFoundRestResource;
import net.tokensmith.otter.gateway.Configure;
import net.tokensmith.otter.gateway.Gateway;
import net.tokensmith.otter.gateway.builder.*;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.entity.Target;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.gateway.entity.rest.RestTarget;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.authorization.http.config.HttpAppConfig;
import net.tokensmith.authorization.http.controller.resource.api.publik.model.Health;
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
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class TokenSmithConfig implements Configure {
    public static final String WEB_SITE_GROUP = "WebSite";
    public static final String API_PUBLIC_V1_GROUP = "API_PUBLIC_V1";
    public static final String API_SITE_V1_GROUP = "API_SITE_V1";
    public static MimeType JSON = new MimeTypeBuilder().json().build();
    private ApplicationContext appContext;
    private HttpProperties httpProperties;

    @Override
    public Shape shape() {
        HttpProperties props = httpProperties();

        SymmetricKey csrfKey = new SymmetricKey(
                Optional.of(props.getCsrfKeyId()),
                props.getCsrfKeyValue(),
                Use.SIGNATURE
        );

        SymmetricKey encKey = new SymmetricKey(
                Optional.of(props.getSessionKeyId()),
                props.getSessionKeyValue(),
                Use.ENCRYPTION
        );

        CookieConfig redirectCookieConfig = applicationContext()
                .getBean("redirectConfig", CookieConfig.class);

        var csrfCookieConfig = new CookieConfig.Builder()
            .name(Shape.CSRF_COOKIE_NAME)
            .secure(props.getCookiesSecure())
            .age(-1)
            .httpOnly(true)
            .build();

        var sessionCookieConfig = new CookieConfig.Builder()
            .name(Shape.SESSION_COOKIE_NAME)
            .secure(props.getCookiesSecure())
            .age(-1)
            .httpOnly(true)
            .build();

        return new ShapeBuilder()
            .encKey(encKey)
            .signkey(csrfKey)
            .cookieConfig(redirectCookieConfig)
            .sessionCookieConfig(sessionCookieConfig)
            .csrfCookieConfig(csrfCookieConfig)
            .build();
    }

    @Override
    public List<Group<? extends DefaultSession, ? extends DefaultUser>> groups() {
        List<Group<? extends DefaultSession, ? extends DefaultUser>> groups = new ArrayList<>();

        var serverErrorResource = applicationContext().getBean(ServerErrorResource.class);
        var mediaTypeResource = applicationContext().getBean(MediaTypeResource.class);

        ErrorTarget<WebSiteSession, WebSiteUser> mediaType = new ErrorTargetBuilder<WebSiteSession, WebSiteUser>()
                .resource(mediaTypeResource)
                .build();

        WebSiteAuthRequired authRequired = applicationContext().getBean(WebSiteAuthRequired.class);
        CSPBetween cspBetween = new CSPBetween();

        HttpProperties props = httpProperties();

        Group<WebSiteSession, WebSiteUser> webSiteGroup = new GroupBuilder<WebSiteSession, WebSiteUser>()
            .name(WEB_SITE_GROUP)
            .sessionClazz(WebSiteSession.class)
            .onError(StatusCode.SERVER_ERROR, serverErrorResource)
            .onDispatchError(StatusCode.UNSUPPORTED_MEDIA_TYPE, mediaType)
            .before(Label.AUTH_REQUIRED, authRequired)
            .after(cspBetween)
            .onHalt(Halt.SESSION, (Response<WebSiteSession> response, HaltException e) -> {
                AssetPresenter presenter = new AssetPresenter();
                presenter.setGlobalCssPath(props.globalCssPath());
                response.setPresenter(Optional.of(presenter));
                response.setTemplate(Optional.of("/WEB-INF/jsp/401.jsp"));
                response.setStatusCode(StatusCode.UNAUTHORIZED);
                response.getCookies().remove(Shape.SESSION_COOKIE_NAME);
                return response;
            })
            .onHalt(Halt.CSRF, (Response<WebSiteSession> response, HaltException e) -> {
                AssetPresenter presenter = new AssetPresenter();
                presenter.setGlobalCssPath(props.globalCssPath());
                response.setPresenter(Optional.of(presenter));
                response.setTemplate(Optional.of("/WEB-INF/jsp/403.jsp"));
                response.setStatusCode(StatusCode.FORBIDDEN);
                response.getCookies().remove(Shape.CSRF_COOKIE_NAME);
                return response;
            })
            .build();

        groups.add(webSiteGroup);

        return groups;
    }

    @Override
    public List<RestGroup<? extends DefaultSession, ? extends DefaultUser>> restGroups() {
        List<RestGroup<?extends DefaultSession, ? extends DefaultUser>> restGroups = new ArrayList<>();

        // uses default bad request handling.
        RestGroup<DefaultSession, APIUser> apiGroupV1 = new RestGroupBuilder<DefaultSession, APIUser>()
                .name(API_PUBLIC_V1_GROUP)
                .sessionClazz(DefaultSession.class)
                .build();

        restGroups.add(apiGroupV1);

        // apis for the site, update user profile.
        RestSessionAuth authRequired = applicationContext().getBean(RestSessionAuth.class);
        RestGroup<WebSiteSession, WebSiteUser> apiSiteGroupV1 = new RestGroupBuilder<WebSiteSession, WebSiteUser>()
                .name(API_SITE_V1_GROUP)
                .before(Label.AUTH_REQUIRED, authRequired)
                .sessionClazz(WebSiteSession.class)
                .build();

        restGroups.add(apiSiteGroupV1);

        return restGroups;
    }

    @Override
    public void routes(Gateway gateway) {
        ApplicationContext context = applicationContext();
        apiPublicRoutes(gateway, context);
        apiSiteRoutes(gateway, context);
        webSiteRoutes(gateway, context);
    }

    protected ApplicationContext applicationContext() {
        if(Objects.isNull(appContext)) {
            this.appContext = new AnnotationConfigApplicationContext(HttpAppConfig.class);
        }
        return this.appContext;
    }

    protected HttpProperties httpProperties()  {
        if (Objects.isNull(this.httpProperties)) {
            this.httpProperties = applicationContext().getBean(HttpProperties.class);
        }
        return this.httpProperties;
    }


    protected void webSiteRoutes(Gateway gateway, ApplicationContext context) {
        AuthorizationResource authorizationResource = context.getBean(AuthorizationResource.class);
        Target<WebSiteSession, WebSiteUser> authorizationTarget = new TargetBuilder<WebSiteSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .resource(authorizationResource)
                .regex(AuthorizationResource.URL)
                .build();

        gateway.add(authorizationTarget);

        RegisterResource registerResource = context.getBean(RegisterResource.class);
        Target<WebSiteSession, WebSiteUser> registerTarget = new TargetBuilder<WebSiteSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .resource(registerResource)
                .regex(RegisterResource.URL)
                .build();

        gateway.add(registerTarget);

        WelcomeResource welcomeResource = context.getBean(WelcomeResource.class);
        Target<WebSiteSession, WebSiteUser> welcomeTarget = new TargetBuilder<WebSiteSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .method(Method.GET)
                .resource(welcomeResource)
                .regex(WelcomeResource.URL)
                .build();

        gateway.add(welcomeTarget);

        ForgotPasswordResource forgotPasswordResource = context.getBean(ForgotPasswordResource.class);
        Target<WebSiteSession, WebSiteUser> forgotPasswordTarget = new TargetBuilder<WebSiteSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .resource(forgotPasswordResource)
                .regex(ForgotPasswordResource.URL)
                .build();

        gateway.add(forgotPasswordTarget);

        UpdatePasswordResource updatePasswordResource = context.getBean(UpdatePasswordResource.class);
        Target<WebSiteSession, WebSiteUser> updatePasswordTarget = new TargetBuilder<WebSiteSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .resource(updatePasswordResource)
                .regex(UpdatePasswordResource.URL)
                .build();

        gateway.add(updatePasswordTarget);

        ProfileResource profileResource = context.getBean(ProfileResource.class);
        Target<WebSiteSession, WebSiteUser> profileTarget = new TargetBuilder<WebSiteSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .form()
                .authenticate()
                .resource(profileResource)
                .regex(ProfileResource.URL)
                .build();

        gateway.add(profileTarget);


        // this is an api but are not json. Should this be in a separate group?
        TokenResource tokenResource = context.getBean(TokenResource.class);
        Target<TokenSession, APIUser> tokenTarget = new TargetBuilder<TokenSession, APIUser>()
                .groupName(WEB_SITE_GROUP)
                .method(Method.POST)
                .resource(tokenResource)
                .regex(TokenResource.URL)
                .build();

        gateway.add(tokenTarget);

        NotFoundResource notFoundResource = context.getBean(NotFoundResource.class);
        Target<TokenSession, WebSiteUser> notFoundTarget = new TargetBuilder<TokenSession, WebSiteUser>()
                .groupName(WEB_SITE_GROUP)
                .method(Method.GET)
                .method(Method.POST)
                .resource(notFoundResource)
                .regex(NotFoundResource.URL)
                .build();

        gateway.notFound(notFoundTarget);

    }

    protected void apiPublicRoutes(Gateway gateway, ApplicationContext context) {

        RestTarget<DefaultSession, APIUser, Health> healthTarget = new RestTargetBuilder<DefaultSession, APIUser, Health>()
                .groupName(API_PUBLIC_V1_GROUP)
                .method(Method.GET)
                .restResource(new HealthResource())
                .regex(HealthResource.URL)
                .contentType(JSON)
                .payload(Health.class)
                .build();

        gateway.add(healthTarget);

        UserInfoResource userInfoResource = context.getBean(UserInfoResource.class);
        RestTarget<DefaultSession, APIUser, UserInfo> userInfoTarget = new RestTargetBuilder<DefaultSession, APIUser, UserInfo>()
                .groupName(API_PUBLIC_V1_GROUP)
                .method(Method.GET)
                .method(Method.POST)
                .restResource(userInfoResource)
                .regex(UserInfoResource.URL)
                .payload(UserInfo.class)
                .contentType(JSON)
                .build();

        gateway.add(userInfoTarget);

        RSAPublicKeyResource rsaPublicKeyResource = context.getBean(RSAPublicKeyResource.class);
        RestTarget<DefaultSession, APIUser, RSAPublicKey> rsaPublicKeyTarget = new RestTargetBuilder<DefaultSession, APIUser, RSAPublicKey>()
                .groupName(API_PUBLIC_V1_GROUP)
                .method(Method.GET)
                .restResource(rsaPublicKeyResource)
                .regex(RSAPublicKeyResource.URL)
                .contentType(JSON)
                .payload(RSAPublicKey.class)
                .build();

        gateway.add(rsaPublicKeyTarget);

        RSAPublicKeysResource rsaPublicKeysResource = context.getBean(RSAPublicKeysResource.class);

        RestTarget<DefaultSession, APIUser, RSAPublicKey[]> rsaPublicKeysTarget = new RestTargetBuilder<DefaultSession, APIUser, RSAPublicKey[]>()
                .groupName(API_PUBLIC_V1_GROUP)
                .method(Method.GET)
                .restResource(rsaPublicKeysResource)
                .regex(RSAPublicKeysResource.URL)
                .contentType(JSON)
                .payload(RSAPublicKey[].class)
                .build();

        gateway.add(rsaPublicKeysTarget);

        var restNotFoundResource = new NotFoundRestResource<APIUser>();
        RestTarget<DefaultSession, APIUser, ClientError> notFoundTarget = new RestTargetBuilder<DefaultSession, APIUser, ClientError>()
                .groupName(API_PUBLIC_V1_GROUP)
                .crud()
                .restResource(restNotFoundResource)
                .regex("/api/public/v1/(.*)")
                .payload(ClientError.class)
                .build();

        gateway.notFound(notFoundTarget);
    }

    protected void apiSiteRoutes(Gateway gateway, ApplicationContext context) {

        // apis for profile page.
        RestProfileResource restProfileResource = context.getBean(RestProfileResource.class);
        RestTarget<WebSiteSession, WebSiteUser, Profile> profileTarget = new RestTargetBuilder<WebSiteSession, WebSiteUser, Profile>()
                .groupName(API_SITE_V1_GROUP)
                .session()
                .authenticate()
                .method(Method.PUT)
                .crud()
                .restResource(restProfileResource)
                .regex(RestProfileResource.URL)
                .contentType(JSON)
                .payload(Profile.class)
                .build();

        gateway.add(profileTarget);

        RestAddressResource restAddressResource = context.getBean(RestAddressResource.class);
        RestTarget<WebSiteSession, WebSiteUser, Address> addressTarget = new RestTargetBuilder<WebSiteSession, WebSiteUser, Address>()
                .groupName(API_SITE_V1_GROUP)
                .session()
                .authenticate()
                .method(Method.PUT)
                .crud()
                .restResource(restAddressResource)
                .regex(RestAddressResource.URL)
                .contentType(JSON)
                .payload(Address.class)
                .build();

        gateway.add(addressTarget);

        // site not found.
        var restNotFoundResource = new NotFoundRestResource<WebSiteUser>();
        RestTarget<WebSiteSession, WebSiteUser, ClientError> notFoundTarget = new RestTargetBuilder<WebSiteSession, WebSiteUser, ClientError>()
                .groupName(API_SITE_V1_GROUP)
                .crud()
                .restResource(restNotFoundResource)
                .regex("/api/site/v1/(.*)")
                .payload(ClientError.class)
                .build();

        gateway.notFound(notFoundTarget);
    }
}