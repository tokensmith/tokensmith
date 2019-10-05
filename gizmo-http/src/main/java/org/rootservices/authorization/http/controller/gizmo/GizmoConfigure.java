package org.rootservices.authorization.http.controller.gizmo;

import org.rootservices.authorization.http.config.HttpAppConfig;
import org.rootservices.authorization.http.controller.resource.html.ForgotPasswordResource;
import org.rootservices.authorization.http.controller.resource.html.NotFoundResource;
import org.rootservices.authorization.http.controller.resource.html.RegisterResource;
import org.rootservices.authorization.http.controller.resource.html.UpdatePasswordResource;
import org.rootservices.authorization.http.controller.resource.api.RSAPublicKeyResource;
import org.rootservices.authorization.http.controller.resource.api.RSAPublicKeysResource;
import org.rootservices.authorization.http.controller.resource.api.TokenResource;
import org.rootservices.authorization.http.controller.resource.api.UserInfoResource;
import org.rootservices.authorization.http.controller.resource.html.authorization.welcome.WelcomeResource;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Route;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.Optional;

public class GizmoConfigure {

    public void routes(Gateway gateway) {
        /**
        ApplicationContext context = new AnnotationConfigApplicationContext(HttpAppConfig.class);

        AuthorizationResource authorizationResource = context.getBean(AuthorizationResource.class);
        gateway.getCsrfProtect(authorizationResource.URL, authorizationResource);
        gateway.postCsrfProtect(authorizationResource.URL, authorizationResource);

        RegisterResource registerResource = context.getBean(RegisterResource.class);
        gateway.getCsrfProtect(registerResource.URL, registerResource);
        gateway.postCsrfProtect(registerResource.URL, registerResource);

        TokenResource tokenResource = context.getBean(TokenResource.class);
        gateway.post(tokenResource.URL, tokenResource);

        UserInfoResource userInfoResource = context.getBean(UserInfoResource.class);
        gateway.get(userInfoResource.URL, userInfoResource);
        gateway.post(userInfoResource.URL, userInfoResource);

        RSAPublicKeyResource rsaPublicKeyResource = context.getBean(RSAPublicKeyResource.class);
        gateway.get(rsaPublicKeyResource.URL, rsaPublicKeyResource);

        RSAPublicKeysResource rsaPublicKeysResource = context.getBean(RSAPublicKeysResource.class);
        gateway.get(rsaPublicKeysResource.URL, rsaPublicKeysResource);

        WelcomeResource welcomeResource = context.getBean(WelcomeResource.class);
        gateway.get(welcomeResource.URL, welcomeResource);

        ForgotPasswordResource forgotPasswordResource = context.getBean(ForgotPasswordResource.class);
        gateway.getCsrfProtect(forgotPasswordResource.URL, forgotPasswordResource);
        gateway.postCsrfProtect(forgotPasswordResource.URL, forgotPasswordResource);

        UpdatePasswordResource updatePasswordResource = context.getBean(UpdatePasswordResource.class);
        gateway.getCsrfProtect(updatePasswordResource.URL, updatePasswordResource);
        gateway.postCsrfProtect(updatePasswordResource.URL, updatePasswordResource);

        Route notFoundRoute = new RouteBuilder()
                .resource(new NotFoundResource())
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        gateway.setNotFoundRoute(notFoundRoute);
         **/
    }
}
