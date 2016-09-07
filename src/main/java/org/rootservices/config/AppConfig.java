package org.rootservices.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.RequestAuthCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.*;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ClientIdFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ResponseTypesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.CompareConfidentialClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.ValidateParamsCodeGrant;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.AuthRequestFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.context.GetConfidentialClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.request.ComparePublicClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.request.ValidateParamsImplicitGrant;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.request.context.GetPublicClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.RequestAccessToken;
import org.rootservices.jwt.config.AppFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by tommackenzie on 7/4/15.
 */
@Configuration
@ComponentScan("org.rootservices.authorization")
public class AppConfig {
    private static String ALGORITHM = "RSA";
    private static String SHA_256 = "SHA-256";

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
    public Base64.Encoder urlEncoder() {
        return Base64.getUrlEncoder();
    }

    @Bean
    @Scope(value = "prototype")
    public MessageDigest digestSha256() {
        MessageDigest digest = null;
        try {
            digest =  MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digest;
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
        return new ValidateParamsImplicitGrant(
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
        return new ValidateParamsCodeGrant(
                authRequestFactory(),
                compareClientToAuthRequest()
        );
    }

    @Bean
    public RequestAuthCode requestAuthCode() {
        return new RequestAuthCode();
    }

    @Bean
    public RequestAccessToken requestAccessToken() {
        return new RequestAccessToken();
    }

}
