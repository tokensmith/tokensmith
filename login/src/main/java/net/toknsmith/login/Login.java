package net.toknsmith.login;

import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.IdTokenException;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.exception.URLException;
import net.toknsmith.login.exception.http.openid.ErrorResponseException;
import net.toknsmith.login.model.Redirect;
import net.toknsmith.login.model.UserWithTokens;

import java.util.List;


public interface Login {
    UserWithTokens withPassword(String username, String password, List<String> scopes) throws CommException, ErrorResponseException, TranslateException, IdTokenException;
    UserWithTokens withRefreshToken(String refreshToken) throws CommException, ErrorResponseException, TranslateException, IdTokenException;
    UserWithTokens withCode(String code, String nonce, String redirectUri) throws CommException, ErrorResponseException, TranslateException, IdTokenException;
    Redirect authorizationEndpoint(String state, String redirect, List<String> scopes) throws URLException;
    User userInfo(String accessToken) throws CommException, ErrorResponseException, TranslateException, IdTokenException;
}
