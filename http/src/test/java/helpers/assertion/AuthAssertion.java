package helpers.assertion;

import com.ning.http.client.cookie.Cookie;
import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import net.tokensmith.authorization.http.controller.resource.html.authorization.claim.RedirectClaim;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


/**
 * Common assertions to be made across all /authorization tests
 */
public class AuthAssertion {

    /**
     * Makes assertions about the redirect cookie that is set on /authorization
     * @param cookies the entire list of cookies from the response
     * @param exists is the redirect cookie expected to be found.
     * @param expected the expected cookie value
     */
    public void redirectCookie(List<Cookie> cookies, Boolean exists, String expected) {

        Cookie actual = cookies.stream()
                .filter(c -> CookieName.REDIRECT.toString().equals(c.getName()))
                .findAny()
                .orElse(null);

        if (exists) {
            // ensure its there.
            assertThat("redirect cookie was not present", actual, is(notNullValue()));

            // parse the cookie value - its a compact jwt.
            JwtAppFactory jwtAppFactory = new JwtAppFactory();
            JwtSerde jwtSerde = jwtAppFactory.jwtSerde();

            JsonWebToken actualRedirect = null;
            try {
                actualRedirect = jwtSerde.stringToJwt(actual.getValue(), RedirectClaim.class);
            } catch (JsonToJwtException | InvalidJWT e) {
                fail("Unable to parse redirect cookie value to a jwt");
            }

            // ensure its the correct value
            RedirectClaim actualClaim = (RedirectClaim) actualRedirect.getClaims();

            String decodedExpectedRedirect = URLDecoder.decode(expected, StandardCharsets.UTF_8);
            String decodedRedirect = URLDecoder.decode(actualClaim.getRedirect(), StandardCharsets.UTF_8);
            assertThat(decodedRedirect, is(decodedExpectedRedirect));
            assertThat(actualClaim.getIssuedAt(), is(notNullValue()));
        } else {
            // should not be there.
            assertThat(actual, is(nullValue()));
        }
    }

    public String contextWithParams(String context, Map<String, String> params) {
        StringBuilder pathBuilder = new StringBuilder()
                .append(context)
                .append("?");

        params.forEach((k, v) -> {
            pathBuilder
                    .append(k)
                    .append("=")
                    .append(v)
                    .append("&");
        });

        String path = pathBuilder.toString();
        if (path.endsWith("&") || path.endsWith("?")) {
            // pull trailing & or ? from path.
            path = path.substring(0, path.length()-1);
        }

        return path;
    }
}
