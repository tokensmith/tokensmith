package helper.driver;

import net.toknsmith.login.Login;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.model.UserWithTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Driver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

    public static void main(String[] args) {
        LoginFactory loginFactory = new LoginFactory();
        Login login = loginFactory.tokenSmithLogin();

        PasswordExample passwordExample = new PasswordExample(login);
        UserWithTokens userWithTokens = null;
        try {
            userWithTokens = passwordExample.runExample(args[0], args[1]);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }

        System.exit(0);
    }

}
