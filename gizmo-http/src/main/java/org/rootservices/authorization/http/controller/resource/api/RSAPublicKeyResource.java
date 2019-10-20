package org.rootservices.authorization.http.controller.resource.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.http.controller.security.APIUser;
import org.rootservices.authorization.openId.jwk.GetKeys;
import org.rootservices.authorization.openId.jwk.entity.RSAPublicKey;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.controller.header.ContentType;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.router.entity.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class RSAPublicKeyResource extends RestResource<APIUser, RSAPublicKey>{
    private static final Logger logger = LogManager.getLogger(RSAPublicKeyResource.class);
    private static String ID_NAME = "id";
    public static String URL = "/api/v1/jwk/rsa/(?<" + ID_NAME + ">" + Regex.UUID.getRegex() + ")";

    private GetKeys getKeys;

    @Autowired
    public RSAPublicKeyResource(GetKeys getKeys) {
        this.getKeys = getKeys;
    }

    @Override
    public RestResponse<RSAPublicKey> get(RestRequest<APIUser, RSAPublicKey> request, RestResponse<RSAPublicKey> response) {
        setDefaultHeaders(response);
        UUID id = UUID.fromString(request.getMatcher().get().group(ID_NAME));

        Optional<RSAPublicKey> key;
        try {
            key = Optional.of(getKeys.getPublicKeyById(id));
        } catch (NotFoundException e) {
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setPayload(Optional.empty());
            return response;
        }


        response.setPayload(key);
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    protected void setDefaultHeaders(RestResponse<RSAPublicKey> response) {
        Map<String, String> headers = new HashMap<>();

        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue());
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_STORE.getValue());
        headers.put(Header.PRAGMA.getValue(), HeaderValue.NO_CACHE.getValue());

        response.getHeaders().putAll(headers);
    }
}
