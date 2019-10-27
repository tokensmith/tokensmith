package config;

import net.tokensmith.authorization.http.config.HttpAppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by tommackenzie on 5/29/16.
 */
@Configuration
@ComponentScan("helpers.fixture.persistence")
@Import(HttpAppConfig.class)
public class TestHttpAppConfig {
}
