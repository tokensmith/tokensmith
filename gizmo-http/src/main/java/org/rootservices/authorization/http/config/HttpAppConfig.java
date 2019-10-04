package org.rootservices.authorization.http.config;


import org.rootservices.config.AppConfig;
import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.authentication.ParseBearer;
import org.rootservices.otter.authentication.ParseHttpBasic;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.translator.JsonTranslator;
import org.springframework.context.annotation.*;


/**
 * Created by tommackenzie on 5/27/16.
 */
@Configuration
@ComponentScan("org.rootservices.authorization.http")
@Import({AppConfig.class, HttpPersistenceConfig.class})
@PropertySource({"classpath:application-${spring.profiles.active:default}.properties"})
public class HttpAppConfig {

    public OtterAppFactory otterAppFactory() {
        return new OtterAppFactory();
    }

    @Bean
    public JsonTranslator jsonTranslator() {
        return otterAppFactory().jsonTranslator();
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
}
