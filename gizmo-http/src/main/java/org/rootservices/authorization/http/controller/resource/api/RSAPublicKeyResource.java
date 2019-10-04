package org.rootservices.authorization.http.controller.resource.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.openId.jwk.GetKeys;
import org.rootservices.authorization.openId.jwk.entity.RSAPublicKey;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.router.entity.Regex;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.ToJsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class RSAPublicKeyResource extends RestResource<RSAPublicKey>{
    private static final Logger logger = LogManager.getLogger(RSAPublicKeyResource.class);
    private static String ID_NAME = "id";
    public static String URL = "/api/v1/jwk/rsa/(?<" + ID_NAME + ">" + Regex.UUID.getRegex() + ")";

    private GetKeys getKeys;
    private ObjectMapper objectMapper;

    @Autowired
    public RSAPublicKeyResource(JsonTranslator<RSAPublicKey> translator, GetKeys getKeys, ObjectMapper objectMapper) {
        super(translator);
        this.getKeys = getKeys;
        this.objectMapper = objectMapper;
    }

    @Override
    public Response get(Request request, Response response) {
        setDefaultHeaders(response);
        UUID id = UUID.fromString(request.getMatcher().get().group(ID_NAME));

        RSAPublicKey key;
        try {
            key = getKeys.getPublicKeyById(id);
        } catch (NotFoundException e) {
            response.setStatusCode(StatusCode.NOT_FOUND);
            return response;
        }

        Optional<ByteArrayOutputStream> payload = Optional.empty();
        try {
            payload = Optional.of(translator.to(key));
        } catch (ToJsonException e) {
            logger.error(e.getMessage(), e);
        }

        response.setPayload(payload);
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    protected void setDefaultHeaders(Response response) {
        Map<String, String> headers = new HashMap<>();

        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), "application/json;charset=UTF-8");
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_STORE.getValue());
        headers.put(Header.PRAGMA.getValue(), HeaderValue.NO_CACHE.getValue());

        response.getHeaders().putAll(headers);
    }
}
