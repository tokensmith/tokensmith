package net.tokensmith.authorization.http.controller.resource.api.publik;

import net.tokensmith.authorization.http.controller.security.APIUser;
import net.tokensmith.authorization.openId.identity.MakeUserInfoIdentityToken;
import net.tokensmith.authorization.openId.identity.exception.IdTokenException;
import net.tokensmith.authorization.openId.identity.exception.KeyNotFoundException;
import net.tokensmith.authorization.openId.identity.exception.ResourceOwnerNotFoundException;
import net.tokensmith.authorization.register.request.UserInfo;
import net.tokensmith.otter.authentication.ParseBearer;
import net.tokensmith.otter.authentication.exception.BearerException;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.ServerError;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.controller.header.AuthScheme;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;
import net.tokensmith.otter.translator.JsonTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Component
public class UserInfoResource extends RestResource<APIUser, UserInfo> {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoResource.class);
    public static String URL = "/api/public/v1/userinfo";

    private JsonTranslator<ServerError> serverErrorTranslator;
    private ParseBearer parseBearer;
    private MakeUserInfoIdentityToken makeUserInfoIdentityToken;

    @Autowired
    public UserInfoResource(JsonTranslator<ServerError> serverErrorTranslator, ParseBearer parseBearer, MakeUserInfoIdentityToken makeUserInfoIdentityToken) {
        this.serverErrorTranslator = serverErrorTranslator;
        this.parseBearer = parseBearer;
        this.makeUserInfoIdentityToken = makeUserInfoIdentityToken;
    }

    @Override
    public RestResponse<UserInfo> get(RestRequest<APIUser, UserInfo> request, RestResponse<UserInfo> response) {
        setDefaultHeaders(response);

        Set<String> accepts = parseHeader(request.getHeaders().get(Header.ACCEPT.getValue()));
        if(!accepts.contains(ContentType.JWT.getValue())) {
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }

        // authorization
        String authHeader = request.getHeaders().get(Header.AUTH.getValue());
        String accessToken;
        try {
            accessToken = parseBearer.parse(authHeader);
        } catch (BearerException e) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.getHeaders().put(Header.AUTH_MISSING.getValue(), AuthScheme.BEARER.getScheme());
            return response;
        }
        
        ByteArrayOutputStream idToken;
        try {
            idToken = makeUserInfoIdentityToken.make(accessToken);
        } catch (ResourceOwnerNotFoundException e) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.getHeaders().put(Header.AUTH_MISSING.getValue(), HeaderValue.INVALID_TOKEN.getValue());
            return response;
        } catch (IdTokenException e) {
            logger.error(e.getMessage(), e);
            response.setStatusCode(StatusCode.SERVER_ERROR);
            return response;
        } catch (KeyNotFoundException e) {
            logger.error(e.getMessage(), e);
            response.setStatusCode(StatusCode.SERVER_ERROR);
            return response;
        }

        response.setStatusCode(StatusCode.OK);
        response.setRawPayload(Optional.of(idToken.toByteArray()));
        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), ContentType.JWT_UTF_8.getValue());

        return response;
    }

    protected Set<String> parseHeader(String header) {
        // type/subtype
        // charset=X

        Set<String> accepts = new HashSet<>();
        if (header != null) {
            String[] values = header.split(";");
            for (String value : values) {
                accepts.add(value);
            }
        }
        return accepts;
    }

    protected void setDefaultHeaders(RestResponse<UserInfo> response) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_STORE.getValue());
        headers.put(Header.PRAGMA.getValue(), HeaderValue.NO_CACHE.getValue());

        response.getHeaders().putAll(headers);
    }
}