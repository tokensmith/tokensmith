package org.rootservices.authorization.http.config;


import org.rootservices.authorization.http.response.Error;
import org.rootservices.authorization.http.response.Token;
import org.rootservices.authorization.register.request.UserInfo;
import org.rootservices.config.AppConfig;
import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.authentication.ParseBearer;
import org.rootservices.otter.authentication.ParseHttpBasic;
import org.rootservices.otter.controller.entity.ClientError;
import org.rootservices.otter.controller.entity.ServerError;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.config.TranslatorAppFactory;
import org.springframework.context.annotation.*;


/**
 * Created by tommackenzie on 5/27/16.
 */
@Configuration
@ComponentScan("org.rootservices.authorization.http")
@Import({AppConfig.class, HttpPersistenceConfig.class})
@PropertySource({"classpath:application-${spring.profiles.active:default}.properties"})
public class HttpAppConfig {

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
}
