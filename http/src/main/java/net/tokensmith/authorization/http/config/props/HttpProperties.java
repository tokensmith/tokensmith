package net.tokensmith.authorization.http.config.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:application-${spring.profiles.active:default}.properties"})
public class HttpProperties {

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
}
