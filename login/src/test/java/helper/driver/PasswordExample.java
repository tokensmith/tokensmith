package helper.driver;

import net.toknsmith.login.exception.IdTokenException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import net.toknsmith.login.Login;
import net.toknsmith.login.model.UserWithTokens;
import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.exception.http.openid.ErrorResponseException;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;

import java.util.ArrayList;
import java.util.List;

public class PasswordExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordExample.class);
    private Login login;

    public PasswordExample(Login login) {
        this.login = login;
    }

    public UserWithTokens runExample(String username, String password) {
        UserWithTokens userWithTokens;
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");

        try {
            userWithTokens = login.withPassword(username, password, scopes);
        } catch (CommException | ErrorResponseException | TranslateException | IdTokenException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Could not login user", e);
        }

        LOGGER.info(userWithTokens.toString());

        User user;
        try {
            user = login.userInfo(userWithTokens.getAccessToken());
        } catch (CommException | ErrorResponseException | TranslateException | IdTokenException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Could not login user", e);
        }

        LOGGER.info(user.toString());

        return userWithTokens;
    }
}
