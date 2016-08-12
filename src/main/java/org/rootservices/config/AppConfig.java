package org.rootservices.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.*;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ClientIdFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ResponseTypesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.CompareConfidentialClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.ValidateParamsCodeResponseType;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.AuthRequestFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.context.GetConfidentialClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.RequestAuthCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.RequestAuthCodeImpl;
import org.rootservices.authorization.oauth2.grant.redirect.token.authorization.request.ComparePublicClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.token.authorization.request.ValidateParamsTokenResponseType;
import org.rootservices.authorization.oauth2.grant.redirect.token.authorization.request.context.GetPublicClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.token.authorization.response.RequestAccessToken;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.CompareConfidentialClientToOpenIdAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.ValidateOpenIdCodeResponseType;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.context.GetOpenIdConfidentialClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.context.GetOpenIdClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.factory.OpenIdAuthRequestFactory;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.factory.required.OpenIdRedirectUriFactory;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.ValidateOpenIdIdTokenResponseType;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.context.GetOpenIdPublicClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.ComparePublicClientToOpenIdAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.OpenIdTokenAuthRequestFactory;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.required.NonceFactory;
import org.rootservices.jwt.config.AppFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tommackenzie on 7/4/15.
 */
@Configuration
@ComponentScan("org.rootservices.authorization")
public class AppConfig {
    private static String ALGORITHM = "RSA";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om =  new ObjectMapper()
            .setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES
            )
            .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
            .registerModule(new Jdk8Module());
        return om;
    }

    @Bean
    public UrlValidator urlValidator() {
        String[] schemes = {"https",};
        return new UrlValidator(schemes);
    }

    @Bean
    public String salt() {
        return System.getenv("AUTH_SALT");
    }

    @Bean
    public KeyPairGenerator keyPairGenerator() throws ConfigException {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigException("Could not create keyPairGenerator. ", e);
        }
        return keyPairGenerator;
    }

    @Bean
    public KeyFactory keyFactory() throws ConfigException {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigException("Could not create KeyFactory. ", e);
        }
        return keyFactory;
    }

    @Bean
    public AppFactory appFactory() {
        return new AppFactory();
    }

    @Bean
    public ClientIdFactory clientIdFactory() {
        return new ClientIdFactory();
    }

    @Bean
    public ResponseTypesFactory responseTypesFactory() {
        return new ResponseTypesFactory();
    }

    @Bean
    public RedirectUriFactory redirectUriFactory() {
        return new RedirectUriFactory();
    }

    @Bean
    public ScopesFactory scopesFactory() {
        return new ScopesFactory();
    }

    @Bean
    public StateFactory stateFactory() {
        return new StateFactory();
    }

    // TOKEN Response Type
    @Bean
    public GetClientRedirectUri getPublicClientRedirectUri() {
        return new GetPublicClientRedirectUri();
    }

    @Bean
    public AuthRequestFactory authRequestFactoryTokenResponseType() {
        AuthRequestFactory authRequestBuilder = new AuthRequestFactory(
                clientIdFactory(),
                responseTypesFactory(),
                redirectUriFactory(),
                scopesFactory(),
                stateFactory(),
                getPublicClientRedirectUri()
        );

        return authRequestBuilder;
    }

    @Bean
    public CompareClientToAuthRequest comparePublicClientToAuthRequest() {
        return new ComparePublicClientToAuthRequest();
    }

    @Bean
    public ValidateParams validateParamsTokenResponseType() {
        return new ValidateParamsTokenResponseType(
                authRequestFactoryTokenResponseType(),
                comparePublicClientToAuthRequest()
        );
    }

    // CODE Response Type. Rename these!
    @Bean
    public GetClientRedirectUri getConfidentialClientRedirectUri() {
        return new GetConfidentialClientRedirectUri();
    }

    @Bean
    public AuthRequestFactory authRequestFactory() {
        return new AuthRequestFactory(
                clientIdFactory(),
                responseTypesFactory(),
                redirectUriFactory(),
                scopesFactory(),
                stateFactory(),
                getConfidentialClientRedirectUri()
        );
    }

    @Bean
    public CompareClientToAuthRequest compareClientToAuthRequest() {
        return new CompareConfidentialClientToAuthRequest();
    }

    @Bean
    public ValidateParams validateParamsCodeResponseType() {
        return new ValidateParamsCodeResponseType(
                authRequestFactory(),
                compareClientToAuthRequest()
        );
    }

    // OpenId Scope and Code Response Type
    @Bean
    public GetOpenIdClientRedirectUri getOpenIdConfidentialClientRedirectUri() {
        return new GetOpenIdConfidentialClientRedirectUri();
    }

    @Bean
    public OpenIdRedirectUriFactory openIdRedirectUriFactory() {
        return new OpenIdRedirectUriFactory();
    }

    @Bean
    public OpenIdAuthRequestFactory openIdAuthRequestFactory() {
        return new OpenIdAuthRequestFactory(
                clientIdFactory(),
                openIdRedirectUriFactory(),
                responseTypesFactory(),
                scopesFactory(),
                stateFactory(),
                getOpenIdConfidentialClientRedirectUri()
        );
    }

    @Bean
    public CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest() {
        return new CompareConfidentialClientToOpenIdAuthRequest();
    }

    @Bean
    public ValidateOpenIdCodeResponseType validateOpenIdCodeResponseType() {
        return new ValidateOpenIdCodeResponseType(
                openIdAuthRequestFactory(),
                compareConfidentialClientToOpenIdAuthRequest()
        );
    }

    // OpenId Scope and Token Response Type
    @Bean
    public NonceFactory nonceFactory() {
        return new NonceFactory();
    }

    @Bean
    public GetOpenIdClientRedirectUri getOpenIdPublicClientRedirectUri() {
        return new GetOpenIdPublicClientRedirectUri();
    }

    @Bean
    public OpenIdAuthRequestFactory openIdAuthRequestFactoryCodeResponseType() {
        return new OpenIdAuthRequestFactory(
                clientIdFactory(),
                openIdRedirectUriFactory(),
                responseTypesFactory(),
                scopesFactory(),
                stateFactory(),
                getOpenIdPublicClientRedirectUri()
        );
    }

    @Bean
    public OpenIdTokenAuthRequestFactory openIdTokenAuthRequestFactory() {
        return new OpenIdTokenAuthRequestFactory(
                openIdAuthRequestFactoryCodeResponseType(),
                nonceFactory()
        );
    }

    @Bean
    public ComparePublicClientToOpenIdAuthRequest comparePublicClientToOpenIdAuthRequest() {
        return new ComparePublicClientToOpenIdAuthRequest();
    }

    @Bean
    public ValidateOpenIdIdTokenResponseType validateOpenIdIdTokenResponseType() {
        return new ValidateOpenIdIdTokenResponseType(
                openIdTokenAuthRequestFactory(),
                comparePublicClientToOpenIdAuthRequest()
        );
    }

    @Bean
    public RequestAuthCode requestAuthCode() {
        return new RequestAuthCodeImpl();
    }

    @Bean
    public RequestAccessToken requestAccessToken() {
        return new RequestAccessToken();
    }

}
