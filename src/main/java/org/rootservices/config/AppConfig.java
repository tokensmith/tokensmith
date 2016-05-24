package org.rootservices.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.AuthRequestBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.optional.*;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.required.ClientIdBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.required.ClientIdBuilderImpl;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.required.ResponseTypeBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.required.ResponseTypeBuilderImpl;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.CompareConfidentialClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.ValidateParamsCodeResponseType;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.AuthRequestBuilderImpl;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.context.GetConfidentialClientRedirectUriImpl;
import org.rootservices.authorization.oauth2.grant.redirect.token.authorization.request.ComparePublicClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.token.authorization.request.ValidateParamsTokenResponseType;
import org.rootservices.authorization.oauth2.grant.redirect.token.authorization.request.context.GetPublicClientRedirectUri;
import org.rootservices.jwt.config.AppFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tommackenzie on 7/4/15.
 */
@Configuration
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
    public ClientIdBuilder clientIdBuilder() {
        return new ClientIdBuilderImpl();
    }

    @Bean
    public ResponseTypeBuilder responseTypeBuilder() {
        return new ResponseTypeBuilderImpl();
    }

    @Bean
    public RedirectUriBuilder redirectUriBuilder() {
        return new RedirectUriBuilderImpl();
    }

    @Bean
    public ScopesBuilder scopesBuilder() {
        return new ScopesBuilderImpl();
    }

    @Bean
    public StateBuilder stateBuilder() {
        return new StateBuilderImpl();
    }

    // TOKEN Response Type
    @Bean
    public GetClientRedirectUri getPublicClientRedirectUri() {
        return new GetPublicClientRedirectUri();
    }

    @Bean
    public AuthRequestBuilder authRequestBuilderTokenResponseType() {
        AuthRequestBuilderImpl authRequestBuilder = new AuthRequestBuilderImpl(
                clientIdBuilder(),
                responseTypeBuilder(),
                redirectUriBuilder(),
                scopesBuilder(),
                stateBuilder(),
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
                authRequestBuilderTokenResponseType(),
                comparePublicClientToAuthRequest()
        );
    }

    // CODE Response Type. Rename these!
    @Bean
    public GetClientRedirectUri getConfidentialClientRedirectUri() {
        return new GetConfidentialClientRedirectUriImpl();
    }

    @Bean
    public AuthRequestBuilder authRequestBuilder() {
        return new AuthRequestBuilderImpl(
                clientIdBuilder(),
                responseTypeBuilder(),
                redirectUriBuilder(),
                scopesBuilder(),
                stateBuilder(),
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
                authRequestBuilder(),
                compareClientToAuthRequest()
        );
    }

}
