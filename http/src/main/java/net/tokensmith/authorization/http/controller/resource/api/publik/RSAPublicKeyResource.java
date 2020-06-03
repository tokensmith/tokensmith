package net.tokensmith.authorization.http.controller.resource.api.publik;


import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.builder.ClientErrorBuilder;
import net.tokensmith.otter.controller.entity.Cause;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;
import net.tokensmith.otter.router.entity.Regex;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.exception.ToJsonException;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RSAPublicKeyResource.class);
    private static String ID_NAME = "id";
    public static String URL = "/api/public/v1/jwk/rsa/(?<" + ID_NAME + ">" + Regex.UUID.getRegex() + ")";

    private JsonTranslator<ClientError> clientErrorTranslator;
    private GetKeys getKeys;

    @Autowired
    public RSAPublicKeyResource(JsonTranslator<ClientError> clientErrorTranslator, GetKeys getKeys) {
        this.clientErrorTranslator = clientErrorTranslator;
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
            response.setRawPayload(notFound("id", id.toString()));
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

    private Optional<byte[]> notFound(String key, String actual) {

        Cause cause = new Cause.Builder()
                .source(Cause.Source.URL)
                .key(key)
                .actual(actual)
                .reason("id was not found")
                .build();

        ClientError clientError = new ClientErrorBuilder()
                .cause(cause)
                .build();

        Optional<byte[]> to = Optional.empty();


        try {
            to = Optional.of(clientErrorTranslator.to(clientError));
        } catch (ToJsonException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return to;
    }
}
