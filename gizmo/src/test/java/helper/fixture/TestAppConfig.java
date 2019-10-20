package helper.fixture;

import org.rootservices.config.AppConfig;
import org.rootservices.config.PersistenceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({"helper"})
@Import({AppConfig.class, PersistenceConfig.class})
public class TestAppConfig {

}
