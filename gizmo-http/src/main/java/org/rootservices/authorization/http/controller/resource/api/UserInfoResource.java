package org.rootservices.authorization.http.controller.resource.api;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.http.response.Error;
import org.rootservices.authorization.openId.identity.MakeUserInfoIdentityToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ResourceOwnerNotFoundException;
import org.rootservices.authorization.register.exception.RegisterException;
import org.rootservices.authorization.register.RegisterOpenIdUser;
import org.rootservices.authorization.register.request.UserInfo;
import org.rootservices.otter.authentication.ParseBearer;
import org.rootservices.otter.authentication.exception.BearerException;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.header.AuthScheme;
import org.rootservices.otter.controller.header.ContentType;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.ToJsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Component
public class UserInfoResource extends RestResource<UserInfo> {
    private static final Logger logger = LogManager.getLogger(UserInfoResource.class);
    public static String URL = "/api/v1/userinfo";

    private ParseBearer parseBearer;
    private MakeUserInfoIdentityToken makeUserInfoIdentityToken;
    private RegisterOpenIdUser registerOpenIdUser;

    @Autowired
    public UserInfoResource(JsonTranslator<UserInfo> jsonTranslator, ParseBearer parseBearer, MakeUserInfoIdentityToken makeUserInfoIdentityToken, RegisterOpenIdUser registerOpenIdUser) {
        super(jsonTranslator);
        this.parseBearer = parseBearer;
        this.makeUserInfoIdentityToken = makeUserInfoIdentityToken;
        this.registerOpenIdUser = registerOpenIdUser;
    }

    @Override
    public Response get(Request request, Response response) {
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

        String idToken;
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

        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        for (int i = 0; i < idToken.length(); ++i)
            payload.write(idToken.charAt(i));

        response.setStatusCode(StatusCode.OK);
        response.setPayload(Optional.of(payload));
        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), ContentType.JWT_UTF_8.getValue());

        return response;
    }

    @Override
    protected Response post(Request request, Response response, UserInfo entity) {
        setDefaultHeaders(response);

        try {
            registerOpenIdUser.run(entity);
        } catch (RegisterException e) {
            logger.debug(e.getMessage(), e);

            Optional<ByteArrayOutputStream> payload = Optional.empty();
            Error error = new Error("Registration Error", e.getMessage());

            try {
                ByteArrayOutputStream out = translator.to(error);
                payload = Optional.of(out);
            } catch (ToJsonException jsonException) {
                logger.error(jsonException.getMessage(), jsonException);
            }

            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setPayload(payload);
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

    protected void setDefaultHeaders(Response response) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_STORE.getValue());
        headers.put(Header.PRAGMA.getValue(), HeaderValue.NO_CACHE.getValue());

        response.getHeaders().putAll(headers);
    }
}
