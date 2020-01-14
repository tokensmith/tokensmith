package net.toknsmith.login;


import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.toknsmith.login.endpoint.UserEndpoint;
import net.toknsmith.login.exception.JwtException;
import net.toknsmith.login.model.Redirect;
import net.toknsmith.login.model.UserWithTokens;
import net.toknsmith.login.exception.*;
import net.toknsmith.login.exception.builder.IdTokenExceptionBuilder;
import net.toknsmith.login.exception.http.openid.ErrorResponseException;
import net.toknsmith.login.factory.MakeRedirect;
import net.toknsmith.login.endpoint.entity.response.openid.OpenIdToken;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import java.util.*;



public class TokenSmithLogin implements Login {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenSmithLogin.class);

    private UserEndpoint userEndpoint;
    private MakeRedirect makeRedirect;
    private LoginUtils loginUtils;

    public TokenSmithLogin(UserEndpoint userEndpoint, MakeRedirect makeRedirect, LoginUtils loginUtils) {
        this.userEndpoint = userEndpoint;
        this.makeRedirect = makeRedirect;
        this.loginUtils = loginUtils;
    }

    /**
     * Login a resource owner with the password grant type
     *
     * @param username user name of resource owner
     * @param password password of resource owner
     * @param scopes scopes to request access to from id server
     * @return a UserWithTokens
     * @throws CommException if the id server could not be reached, when attempting to interact with token endpoint.
     * @throws ErrorResponseException if the response from the id server was not successful, when attempting to interact with token endpoint
     * @throws TranslateException if the response body was not what the sdk expected, when attempting to interact with token endpoint
     * @throws IdTokenException if there was an issue verifying the id_token or getting the public key from the id server.
     */
    @Override
    public UserWithTokens withPassword(String username, String password, List<String> scopes) throws CommException, ErrorResponseException, TranslateException, IdTokenException {
        OpenIdToken openIdToken = userEndpoint.postTokenPasswordGrant(username, password, scopes);

        JsonWebToken jwt = loginUtils.toJwt(openIdToken.getIdToken());

        UserWithTokens userWithTokens;
        try {
            userWithTokens = loginUtils.toUserWithTokens(jwt, openIdToken);
        } catch (JwtException e) {
            throw handleForTokenEndpoint("Issue verifying the id_token", e, openIdToken, jwt);
        }

        return userWithTokens;
    }

    /**
     * Login a resource owner with the refresh token grant type
     *
     * @param refreshToken a valid refresh token issued by the id server
     * @return a UserWithTokens
     * @throws CommException if the id server could not be reached, when attempting to interact with token endpoint.
     * @throws ErrorResponseException if the response from the id server was not successful, when attempting to interact with token endpoint
     * @throws TranslateException if the response body was not what the sdk expected, when attempting to interact with token endpoint
     * @throws IdTokenException if there was an issue verifying the id_token or getting the public key from the id server.
     */
    public UserWithTokens withRefreshToken(String refreshToken) throws CommException, ErrorResponseException, TranslateException, IdTokenException {
        OpenIdToken openIdToken = userEndpoint.postTokenRefreshGrant(refreshToken);
        JsonWebToken jwt = loginUtils.toJwt(openIdToken.getIdToken());

        UserWithTokens userWithTokens;
        try {
            userWithTokens = loginUtils.toUserWithTokens(jwt, openIdToken);
        } catch (JwtException e) {
            throw handleForTokenEndpoint("Issue verifying the id_token", e, openIdToken, jwt);
        }
        return userWithTokens;
    }

    /**
     * Login a resource owner with the authorization grant type
     *
     * @param code authorization code issued by id server
     * @param nonce the nonce passed to the auth request. It's value must match the id_token's nonce value.
     * @param redirectUri the redirect uri registered with the id server
     * @return a UserWithTokens
     * @throws CommException if the id server could not be reached, when attempting to interact with token endpoint.
     * @throws ErrorResponseException if the response from the id server was not successful, when attempting to interact with token endpoint
     * @throws TranslateException if the response body was not what the sdk expected, when attempting to interact with token endpoint
     * @throws IdTokenException if there was an issue verifying the id_token or getting the public key from the id server.
     */
    @Override
    public UserWithTokens withCode(String code, String nonce, String redirectUri) throws CommException, ErrorResponseException, TranslateException, IdTokenException {
        OpenIdToken openIdToken = userEndpoint.postTokenCodeGrant(code, redirectUri);
        JsonWebToken jwt = loginUtils.toJwt(openIdToken.getIdToken());

        UserWithTokens userWithTokens;
        try {
            userWithTokens = loginUtils.toUserWithTokens(jwt, openIdToken);
        } catch (JwtException e) {
            throw handleForTokenEndpoint("Issue verifying the id_token", e, openIdToken, jwt);
        }

        if (!loginUtils.isNonceOk(userWithTokens.getUser(), nonce)) {
            throw handleForTokenEndpoint("Invalid nonce", null, openIdToken, jwt);
        }

        return userWithTokens;
    }

    /**
     * Creates the redirect URI to the id server's authorization endpoint with the response type, code.
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest}">OpenId Auth Request may help</a>
     *
     * @param state used to mitigate CSRF.
     * @param redirect the uri to redirect the resource owner to after they authorize with id server
     * @param scopes the scopes to ask the resource owner to authorize.
     * @return and instance of Redirect with nonce and state
     * @throws URLException
     */
    @Override
    public Redirect authorizationEndpoint(String state, String redirect, List<String> scopes) throws URLException {
        return makeRedirect.makeRedirect(state, redirect, scopes);
    }

    /**
     * Get the claims about the authenticated resource owner.
     *
     * @param accessToken an access token issued by the id server
     * @return the user associated with the access token
     * @throws CommException if the id server could not be reached, when attempting to interact with userinfo endpoint.
     * @throws ErrorResponseException if the response from the id server was not successful, when attempting to interact with userinfo endpoint
     * @throws TranslateException if the response body was not what the sdk expected, when attempting to interact with userinfo endpoint
     * @throws IdTokenException if there was an issue verifying the id_token or getting the public key from the id server.
     */
    @Override
    public User userInfo(String accessToken) throws CommException, ErrorResponseException, TranslateException, IdTokenException {
        JsonWebToken idToken = userEndpoint.getUser(accessToken);

        User user;
        try {
            user = loginUtils.toUser(idToken);
        } catch (JwtException e) {
            throw handleForUserInfoEndpoint("Issue verifying the id_token", e, idToken);
        }

        return user;
    }

    protected IdTokenException handleForTokenEndpoint(String msg, Throwable cause, OpenIdToken openIdToken, JsonWebToken idToken) {
        return new IdTokenExceptionBuilder()
                .message(msg)
                .cause(cause)
                .fromTokenEndpoint(openIdToken)
                .user((User) idToken.getClaims())
                .build();
    }

    protected IdTokenException handleForUserInfoEndpoint(String msg, Throwable cause, JsonWebToken idToken) {
        return new IdTokenExceptionBuilder()
                .message(msg)
                .cause(cause)
                .user((User) idToken.getClaims())
                .build();
    }
}
