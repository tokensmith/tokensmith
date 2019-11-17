package net.tokensmith.authorization.http.controller.resource.api;


import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;
import net.tokensmith.otter.router.entity.Regex;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import net.tokensmith.authorization.http.controller.security.APIUser;
import net.tokensmith.authorization.openId.jwk.GetKeys;
import net.tokensmith.authorization.openId.jwk.entity.RSAPublicKey;
import net.tokensmith.authorization.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class RSAPublicKeyResource extends RestResource<APIUser, RSAPublicKey> {
    private static final Logger logger = LoggerFactory.getLogger(RSAPublicKeyResource.class);
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
