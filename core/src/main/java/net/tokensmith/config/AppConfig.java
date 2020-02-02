package net.tokensmith.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.parser.Parser;
import net.tokensmith.parser.config.ParserConfig;
import net.tokensmith.pelican.config.PelicanAppConfig;
import org.apache.commons.validator.routines.UrlValidator;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.request.CompareConfidentialClientToAuthRequest;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.request.context.GetConfidentialClientRedirectUri;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.request.context.GetPublicClientRedirectUri;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.pelican.Publish;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;


/**
 * Created by tommackenzie on 7/4/15.
 */
@Configuration
@ComponentScan({"net.tokensmith.authorization", "net.tokensmith.pelican"})
@PropertySource({"classpath:application-${spring.profiles.active:default}.properties"})
public class AppConfig {
    private static String ALGORITHM = "RSA";
    private static String SHA_256 = "SHA-256";

    @Value("${allowLocalUrls}")
    private Boolean allowLocalUrls;
    @Value("${allowHttpUrls}")
    private Boolean allowHttpUrls;
    @Value("${issuer}")
    private String issuer;
    @Value("${db.key.id}")
    private String keyId;
    @Value("${db.key.secret}")
    private String secret;

    @Value("${messaging.clientId}")
    private String clientId;
    @Value("${messaging.host}")
    private String messageQueueHost;

    @Value("${session.expiration:2592000}")
    private String sessionExpirationInSeconds;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om =  new ObjectMapper()
            .setPropertyNamingStrategy(
                PropertyNamingStrategy.SNAKE_CASE
            )
            .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
        return om;
    }

    public long urlValidatorOpts() {
        long opts = 0;
        if (allowLocalUrls) {
            opts += UrlValidator.ALLOW_LOCAL_URLS;
        }
        return opts;
    }

    public String[] urlValidatorSchemes() {
        if (allowHttpUrls) {
            return new String[] {"http", "https"};

        }
        return new String[] {"https"};
    }

    @Bean
    public UrlValidator urlValidator() {
        long opts = urlValidatorOpts();
        String[] schemes = urlValidatorSchemes();

        if (opts > 0) {
            return new UrlValidator(schemes, opts);
        } else {
            return new UrlValidator(schemes);
        }
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
    public JwtAppFactory jwtAppFactory() {
        return new JwtAppFactory();
    }

    // TOKEN Response Type
    @Bean
    public GetClientRedirectUri getPublicClientRedirectUri() {
        return new GetPublicClientRedirectUri();
    }

    // CODE Response Type. Rename these!
    @Bean
    public GetClientRedirectUri getConfidentialClientRedirectUri() {
        return new GetConfidentialClientRedirectUri();
    }

    @Bean
    public CompareClientToAuthRequest compareClientToAuthRequest() {
        return new CompareConfidentialClientToAuthRequest();
    }

    @Bean
    public String issuer() {
        return this.issuer;
    }

    @Bean
    public Publish publish() {
        PelicanAppConfig pelicanAppConfig = new PelicanAppConfig();
        pelicanAppConfig.setMessageQueueHost(messageQueueHost);
        return pelicanAppConfig.publish(clientId);
    }

    @Bean
    @Qualifier("dbKey")
    public SymmetricKey dbKey() {
        return new SymmetricKey(
                Optional.of(keyId), secret, Use.ENCRYPTION
        );
    }

    @Bean
    @Qualifier("sessionExpirationInSeconds")
    public Long sessionExpirationInSeconds() {
        return Long.valueOf(sessionExpirationInSeconds);
    }

    @Bean
    public Parser parser() {
        return new ParserConfig().parser();
    }
}
