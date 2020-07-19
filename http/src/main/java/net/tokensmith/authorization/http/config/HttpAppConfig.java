package net.tokensmith.authorization.http.config;


import net.tokensmith.authorization.http.config.props.HttpProperties;
import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import net.tokensmith.authorization.http.response.Error;
import net.tokensmith.authorization.http.response.Token;
import net.tokensmith.authorization.register.request.UserInfo;
import net.tokensmith.config.AppConfig;
import net.tokensmith.jwt.builder.compact.UnsecureCompactBuilder;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.otter.QueryStringToMap;
import net.tokensmith.otter.authentication.ParseBearer;
import net.tokensmith.otter.authentication.ParseHttpBasic;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.ServerError;
import net.tokensmith.otter.security.cookie.CookieSecurity;
import net.tokensmith.otter.security.cookie.CookieSigner;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.Optional;


/**
 * Created by tommackenzie on 5/27/16.
 */
@Configuration
@ComponentScan({"net.tokensmith.authorization.http"})
@Import({AppConfig.class, HttpPersistenceConfig.class})
@PropertySource({"classpath:application-${spring.profiles.active:default}.properties"})
public class HttpAppConfig {

    @Autowired
    private HttpProperties httpProperties;

    private TranslatorAppFactory translatorAppFactory() {
        return new TranslatorAppFactory();
    }

    @Bean
    public QueryStringToMap queryStringToMap() {
        return new QueryStringToMap();
    }

    @Bean
    public ParseBearer parseBearer() {
        return new ParseBearer();
    }

    @Bean
    public ParseHttpBasic parseHttpBasic() {
        return new ParseHttpBasic();
    }

    @Bean
    public JsonTranslator<Error> errorTranslator() {
        return translatorAppFactory().jsonTranslator(Error.class);
    }

    @Bean
    public JsonTranslator<ClientError> clientErrorTranslator() {
        return translatorAppFactory().jsonTranslator(ClientError.class);
    }

    @Bean
    public JsonTranslator<ServerError> serverErrorTranslator() {
        return translatorAppFactory().jsonTranslator(ServerError.class);
    }

    @Bean
    public JsonTranslator<Token> tokenTranslator() {
        return translatorAppFactory().jsonTranslator(Token.class);
    }

    @Bean
    public JsonTranslator<UserInfo> userTranslator() {
        return translatorAppFactory().jsonTranslator(UserInfo.class);
    }

    @Bean
    public UnsecureCompactBuilder unsecureCompactBuilder() {
        return new UnsecureCompactBuilder();
    }

    @Bean
    public CookieSecurity cookieSigner() {
        SymmetricKey key = new SymmetricKey.Builder()
                .keyId(Optional.of(httpProperties.getCookieSignKeyId()))
                .key(httpProperties.getCookieSignKeyValue())
                .use(Use.SIGNATURE)
                .build();

        Map<String, SymmetricKey> keys = Map.ofEntries(
                Map.entry(httpProperties.getCookieSignKeyId(), key)
        );

        Map<String, String> keyPreferences = Map.ofEntries(
                Map.entry(CookieName.REDIRECT.toString(), key.getKeyId().get())
        );
        return new CookieSigner(new JwtAppFactory(), keys, keyPreferences);
    }

    @Bean
    public CookieConfig redirectConfig() {
        return new CookieConfig.Builder()
                .name(CookieName.REDIRECT.toString())
                .httpOnly(true)
                .secure(httpProperties.getCookiesSecure())
                .age(-1)
                .build();
    }
}
