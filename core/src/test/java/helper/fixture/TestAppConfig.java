package helper.fixture;

import net.tokensmith.config.AppConfig;
import net.tokensmith.config.PersistenceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({"helper"})
@Import({AppConfig.class, PersistenceConfig.class})
public class TestAppConfig {

}
