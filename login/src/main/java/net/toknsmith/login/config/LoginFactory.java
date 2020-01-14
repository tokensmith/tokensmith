package net.toknsmith.login.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.toknsmith.login.HttpUtils;
import net.toknsmith.login.cache.KeyCacheLoader;
import net.toknsmith.login.LoginUtils;
import net.toknsmith.login.config.exception.StartUpException;
import net.toknsmith.login.config.props.EndpointProps;
import net.toknsmith.login.endpoint.entity.response.openid.OpenIdToken;
import net.toknsmith.login.translator.*;
import net.toknsmith.login.Login;
import net.toknsmith.login.TokenSmithLogin;
import net.toknsmith.login.endpoint.KeyEndpoint;
import net.toknsmith.login.endpoint.UserEndpoint;
import net.toknsmith.login.factory.MakeRedirect;
import net.toknsmith.login.security.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class LoginFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFactory.class);

    private static String ENV_VAR_MISSING = "environment variable: %s is missing or blank.";
    public static String CLIENT_USER_NAME = "CLIENT_USER_NAME";
    public static String CLIENT_ID = "CLIENT_ID";
    public static String CLIENT_PASSWORD = "CLIENT_PASSWORD";
    public static String TOKEN_URL = "TOKEN_URL";
    public static String USER_INFO_URL = "USER_INFO_URL";
    public static String AUTHORIZATION_URL = "AUTHORIZATION_URL";
    public static String PUBLIC_KEY_URL = "PUBLIC_KEY_URL";
    public static String CORRELATION_ID_FIELD = "CORRELATION_ID_FIELD";
    private static Map<String, String> SECRETS = new HashMap<>();
    private ObjectMapper objectMapper;
    private ObjectReader openIdTokenReader;

    public void setSecrets(Map<String, String> secrets) {
        SECRETS = secrets;
    }

    public ObjectMapper objectMapper() {
        if (objectMapper==null) {
            this.objectMapper = new ObjectMapper()
                    .setPropertyNamingStrategy(
                            PropertyNamingStrategy.SNAKE_CASE
                    )
                    .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
                    .registerModule(new Jdk8Module())
                    .registerModule(new JavaTimeModule())
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        }
        return this.objectMapper;
    }

    public <T> ObjectReader readerFor(Class<T> clazz) {
        return objectMapper().readerFor(clazz);
    }

    public ObjectReader openIdTokenReader() {
        if (openIdTokenReader == null) {
            openIdTokenReader = readerFor(OpenIdToken.class);
        }
        return openIdTokenReader;
    }

    public HttpUtils httpUtils() {
        return new HttpUtils(correlationIdField());
    }

    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    protected Optional<String> getSecret(String key) {
        Optional<String> secret = Optional.empty();
        String value =  SECRETS.get(key);
        if (value != null) {
            secret = Optional.of(value);
        }
        return secret;
    }

    protected void checkSecret(String var, String value) {
        if (value == null || value.isEmpty()) {
            throw new StartUpException(String.format(ENV_VAR_MISSING, var));
        }
    }

    public String correlationIdField() {
        String correlationIdField;
        Optional<String> secret = getSecret(CORRELATION_ID_FIELD);

        if(secret.isEmpty()) {
            correlationIdField = System.getenv(CORRELATION_ID_FIELD);
        } else {
            correlationIdField = secret.get();
        }
        checkSecret(CORRELATION_ID_FIELD, correlationIdField);

        return correlationIdField;
    }
    public String clientId() {
        String clientId;
        Optional<String> secret = getSecret(CLIENT_ID);

        if(secret.isEmpty()) {
            clientId = System.getenv(CLIENT_ID);
        } else {
            clientId = secret.get();
        }
        checkSecret(CLIENT_ID, clientId);

        return clientId;
    }

    public String clientUserName() {
        String clientUserName;
        Optional<String> secret = getSecret(CLIENT_USER_NAME);

        if (secret.isEmpty()) {
            clientUserName = System.getenv(CLIENT_USER_NAME);
        } else {
            clientUserName = secret.get();
        }
        checkSecret(CLIENT_USER_NAME, clientUserName);

        return clientUserName;
    }

    public String clientPassword() {
        String clientPassword;
        Optional<String> secret = getSecret(CLIENT_PASSWORD);
        if (secret.isEmpty()) {
            clientPassword = System.getenv(CLIENT_PASSWORD);
        } else {
            clientPassword = secret.get();
        }
        checkSecret(CLIENT_PASSWORD, clientPassword);
        return clientPassword;
    }

    public BasicAuth basicAuth() {
        return new BasicAuth(Base64.getEncoder());
    }

    public String tokenURL() {
        String tokenUrl;
        Optional<String> secret = getSecret(TOKEN_URL);

        if (secret.isEmpty()) {
            tokenUrl = System.getenv(TOKEN_URL);
        } else {
            tokenUrl = secret.get();
        }
        checkSecret(TOKEN_URL, tokenUrl);
        return tokenUrl;
    }

    public String userInfoURL() {
        String userInfoUrl;
        Optional<String> secret = getSecret(USER_INFO_URL);
        if (secret.isEmpty()) {
            userInfoUrl = System.getenv(USER_INFO_URL);
        } else {
            userInfoUrl = secret.get();
        }
        checkSecret(USER_INFO_URL, userInfoUrl);
        return userInfoUrl;
    }

    public String authorizationURL() {
        String authorizationUrl;
        Optional<String> secret = getSecret(AUTHORIZATION_URL);
        if (secret.isEmpty()) {
            authorizationUrl = System.getenv(AUTHORIZATION_URL);
        } else {
            authorizationUrl = secret.get();
        }
        checkSecret(AUTHORIZATION_URL, authorizationUrl);
        return authorizationUrl;
    }

    public EndpointProps endpointProps() {
        EndpointProps ep = new EndpointProps();
        ep.setTokenEndpoint(to("token url", tokenURL()));
        ep.setUserInfoEndpoint(to("user info url", userInfoURL()));
        ep.setClientId(clientId());
        ep.setAuthorizationUrl(to("authorization url", authorizationURL()));
        ep.setClientCredentials(basicAuth().encodeCredentials(clientUserName(), clientPassword()));

        return ep;
    }

    protected URI to(String name, String from) throws StartUpException {
        URI to;
        try {
            to = new URI(from);
        } catch (URISyntaxException e) {
            String message = String.format(
                "Failed to created URI for, %s with the value, %s", name, from
            );
            LOGGER.error(message);
            throw new StartUpException(message, e);
        }
        return to;
    }

    public JwtAppFactory jwtAppFactory() {
        return new JwtAppFactory();
    }


    public ErrorResponseTranslator errorResponseTranslator() {
        return new ErrorResponseTranslator(objectMapper());
    }

    public ErrorResponseExceptionFactory errorResponseExceptionFactory() {
        return new ErrorResponseExceptionFactory(errorResponseTranslator());
    }

    public UserEndpoint userEndpoint() {
        EndpointProps endpointProps = endpointProps();
        return new UserEndpoint(
                httpClient(),
                httpUtils(),
                endpointProps,
                errorResponseExceptionFactory(),
                openIdTokenReader(),
                jwtAppFactory()
        );
    }

    public JwtRSAPublicKeyTranslator jwtRSAPublicKeyTranslator() {
        return new JwtRSAPublicKeyTranslator(objectMapper());
    }

    public String publicKeyURL() {
        String publicKeyUrl;
        Optional<String> secret = getSecret(PUBLIC_KEY_URL);
        if (secret.isEmpty()) {
            publicKeyUrl = System.getenv(PUBLIC_KEY_URL);
        } else {
            publicKeyUrl = secret.get();
        }
        checkSecret(PUBLIC_KEY_URL, publicKeyUrl);

        // test its a ok URI..
        UUID testKeyId = UUID.randomUUID();
        try {
            to(PUBLIC_KEY_URL, testKeyId.toString());
        } catch (StartUpException e) {
            throw new StartUpException("Unable to create URL for PUBLIC_KEY_URL", e);
        }

        return publicKeyUrl;
    }

    public KeyEndpoint keyEndpoint() {
        return new KeyEndpoint(jwtRSAPublicKeyTranslator(), httpClient(), httpUtils(), publicKeyURL());
    }

    public MakeRedirect makeRedirect() {
        return new MakeRedirect(endpointProps(), new RandomString());
    }

    public LoginUtils makeLoginUtils() {
        return new LoginUtils(keyCache(), jwtAppFactory());
    }

    public Login tokenSmithLogin() {
        return new TokenSmithLogin(userEndpoint(), makeRedirect(), makeLoginUtils());
    }

    public KeyCacheLoader keyCacheLoader() {
        return new KeyCacheLoader(keyEndpoint());
    }

    public LoadingCache<String, RSAPublicKey> keyCache() {
        return Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build(keyCacheLoader()::load);
    }
}
