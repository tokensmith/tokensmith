package integration;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;


/**
 * Created by tommackenzie on 2/4/15.
 */
@ContextConfiguration("classpath:cucumber.xml")
@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        format = {"pretty"},
        features ={"classpath:integration/auth_request_factory.feature"}
)
public class CukeRunnerBDD {
}
