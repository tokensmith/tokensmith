package org.rootservices.authorization.http.controller.gizmo;

import org.rootservices.authorization.http.config.HttpAppConfig;
import org.rootservices.authorization.http.controller.resource.ForgotPasswordResource;
import org.rootservices.authorization.http.controller.resource.NotFoundResource;
import org.rootservices.authorization.http.controller.resource.RegisterResource;
import org.rootservices.authorization.http.controller.resource.UpdatePasswordResource;
import org.rootservices.authorization.http.controller.resource.api.RSAPublicKeyResource;
import org.rootservices.authorization.http.controller.resource.api.RSAPublicKeysResource;
import org.rootservices.authorization.http.controller.resource.api.TokenResource;
import org.rootservices.authorization.http.controller.resource.api.UserInfoResource;
import org.rootservices.authorization.http.controller.resource.authorization.AuthorizationResource;
import org.rootservices.authorization.http.controller.resource.authorization.welcome.WelcomeResource;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Route;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.Optional;

public class GizmoConfigure implements Configure {

    @Override
    public void configure(Gateway gateway) {
        // CSRF cookie configuration
        CookieConfig csrfCookieConfig = new CookieConfig("csrf", false, -1);
        gateway.setCsrfCookieConfig(csrfCookieConfig);
        gateway.setCsrfFormFieldName("csrfToken");

        // Session cookie configuration.
        CookieConfig sessionCookieConfig = new CookieConfig("session", false, -1);
        gateway.setSessionCookieConfig(sessionCookieConfig);

        // CSRF key configuration.
        SymmetricKey csrfKey = new SymmetricKey(
                Optional.of("key-1"),
                "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
                Use.SIGNATURE
        );
        gateway.setSignKey(csrfKey);

        //Session key configuration.
        SymmetricKey encKey = new SymmetricKey(
                Optional.of("key-2"),
                "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
                Use.ENCRYPTION
        );
        gateway.setEncKey(encKey);
    }

    @Override
    public void routes(Gateway gateway) {
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
    }
}
