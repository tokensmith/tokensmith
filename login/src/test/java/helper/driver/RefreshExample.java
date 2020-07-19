package helper.driver;

import net.toknsmith.login.Login;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.IdTokenException;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.exception.http.openid.ErrorResponseException;
import net.toknsmith.login.model.UserWithTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshExample.class);
    private Login login;

    public RefreshExample(Login login) {
        this.login = login;
    }

    public void runExample(String refreshToken) {

        UserWithTokens userWithTokens;

        try {
            userWithTokens = login.withRefreshToken(refreshToken);
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
    }
}
