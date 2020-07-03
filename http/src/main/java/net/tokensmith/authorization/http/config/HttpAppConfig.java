package net.tokensmith.authorization.http.config;


import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import net.tokensmith.jwt.builder.compact.UnsecureCompactBuilder;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.otter.QueryStringToMap;
import net.tokensmith.otter.authentication.ParseBearer;
import net.tokensmith.otter.authentication.ParseHttpBasic;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.ServerError;
import net.tokensmith.otter.security.cookie.CookieSecurity;
import net.tokensmith.otter.security.cookie.CookieSigner;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import net.tokensmith.authorization.http.response.Error;
import net.tokensmith.authorization.http.response.Token;
import net.tokensmith.authorization.register.request.UserInfo;
import net.tokensmith.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by tommackenzie on 5/27/16.
 */
@Configuration
@ComponentScan({"net.tokensmith.authorization.http", "net.tokensmith.authorization.http.config.converter"})
@Import({AppConfig.class, HttpPersistenceConfig.class})
@PropertySource({"classpath:application-${spring.profiles.active:default}.properties"})
public class HttpAppConfig {

    @Value("${cookies.secure}")
    private Boolean cookiesSecure;

    // keys for security cookies, CSRF and Session
    @Value("${csrf.key.id}")
    private String csrfKeyId;

    @Value("${csrf.key.value}")
    private String csrfKeyValue;

    @Value("${session.key.id}")
    private String sessionKeyId;

    @Value("${session.key.value}")
    private String sessionKeyValue;

    // I couldn't figure out how to get a list or map of keys so
    // here is this...
    @Value("${cookies.keys.key-1.id}")
    private String cookieSignKeyId;

    @Value("${cookies.keys.key-1.value}")
    private String cookieSignKeyValue;

    @Value("${assets.css.global:/assets/css/global.css}")
    private String globalCssPath;

    public Boolean getCookiesSecure() {
        return cookiesSecure;
    }

    public String getCsrfKeyId() {
        return csrfKeyId;
    }

    public String getCsrfKeyValue() {
        return csrfKeyValue;
    }

    public String getSessionKeyId() {
        return sessionKeyId;
    }

    public String getSessionKeyValue() {
        return sessionKeyValue;
    }

    public String getCookieSignKeyId() {
        return cookieSignKeyId;
    }

    public String getCookieSignKeyValue() {
        return cookieSignKeyValue;
    }

    @Bean
    public String globalCssPath() {
        return globalCssPath;
    }

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
                .keyId(Optional.of(getCookieSignKeyId()))
                .key(getCookieSignKeyValue())
                .use(Use.SIGNATURE)
                .build();

        Map<String, SymmetricKey> keys = Map.ofEntries(
                Map.entry(getCookieSignKeyId(), key)
        );

        Map<String, String> keyPreferences = Map.ofEntries(
                Map.entry(CookieName.REDIRECT.toString(), key.getKeyId().get())
        );
        return new CookieSigner(new JwtAppFactory(), keys, keyPreferences);
    }
}
