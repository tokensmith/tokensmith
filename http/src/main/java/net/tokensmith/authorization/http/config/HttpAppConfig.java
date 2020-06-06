package net.tokensmith.authorization.http.config;


import net.tokensmith.jwt.builder.compact.UnsecureCompactBuilder;
import net.tokensmith.otter.QueryStringToMap;
import net.tokensmith.otter.authentication.ParseBearer;
import net.tokensmith.otter.authentication.ParseHttpBasic;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.ServerError;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import net.tokensmith.authorization.http.response.Error;
import net.tokensmith.authorization.http.response.Token;
import net.tokensmith.authorization.register.request.UserInfo;
import net.tokensmith.config.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;


/**
 * Created by tommackenzie on 5/27/16.
 */
@Configuration
@ComponentScan("net.tokensmith.authorization.http")
@Import({AppConfig.class, HttpPersistenceConfig.class})
@PropertySource({"classpath:application-${spring.profiles.active:default}.properties"})
public class HttpAppConfig {

    @Value("${cookies.secure}")
    private Boolean cookiesSecure;

    @Value("${csrf.key.id}")
    private String csrfKeyId;

    @Value("${csrf.key.value}")
    private String csrfKeyValue;

    @Value("${session.key.id}")
    private String sessionKeyId;

    @Value("${session.key.value}")
    private String sessionKeyValue;

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
}
