package org.rootservices.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.commons.validator.routines.UrlValidator;
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
}
