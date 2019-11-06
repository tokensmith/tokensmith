package net.tokensmith.authorization.http.controller.resource.api;

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
import net.tokensmith.otter.translator.exception.ToJsonException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import net.tokensmith.authorization.http.controller.security.APIUser;
import net.tokensmith.authorization.openId.identity.MakeUserInfoIdentityToken;
import net.tokensmith.authorization.openId.identity.exception.IdTokenException;
import net.tokensmith.authorization.openId.identity.exception.KeyNotFoundException;
import net.tokensmith.authorization.openId.identity.exception.ResourceOwnerNotFoundException;
import net.tokensmith.authorization.register.exception.RegisterException;
import net.tokensmith.authorization.register.RegisterOpenIdUser;
import net.tokensmith.authorization.register.request.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.*;


@Component
public class UserInfoResource extends RestResource<APIUser, UserInfo> {
    private static final Logger logger = LogManager.getLogger(UserInfoResource.class);
    public static String URL = "/api/v1/userinfo";

    private JsonTranslator<ServerError> serverErrorTranslator;
    private ParseBearer parseBearer;
    private MakeUserInfoIdentityToken makeUserInfoIdentityToken;
    private RegisterOpenIdUser registerOpenIdUser;

    @Autowired
    public UserInfoResource(JsonTranslator<ServerError> serverErrorTranslator, ParseBearer parseBearer, MakeUserInfoIdentityToken makeUserInfoIdentityToken, RegisterOpenIdUser registerOpenIdUser) {
        this.serverErrorTranslator = serverErrorTranslator;
        this.parseBearer = parseBearer;
        this.makeUserInfoIdentityToken = makeUserInfoIdentityToken;
        this.registerOpenIdUser = registerOpenIdUser;
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

    @Override
    public RestResponse<UserInfo> post(RestRequest<APIUser, UserInfo> request, RestResponse<UserInfo> response) {
        setDefaultHeaders(response);

        try {
            registerOpenIdUser.run(request.getPayload().get());
        } catch (RegisterException e) {
            logger.debug(e.getMessage(), e);

            Optional<byte[]> payload = Optional.empty();
            ServerError serverError = new ServerError("Registration Error");

            try {
                byte[] out = serverErrorTranslator.to(serverError);
                payload = Optional.of(out);
            } catch (ToJsonException jsonException) {
                logger.error(jsonException.getMessage(), jsonException);
            }

            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setRawPayload(payload);
            response.getHeaders().put(Header.CONTENT_TYPE.getValue(), ContentType.JWT_UTF_8.getValue());
            return response;
        }

        response.setStatusCode(StatusCode.CREATED);
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
