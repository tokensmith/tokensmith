package org.rootservices.authorization.http.controller.resource.api;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.http.controller.exception.BadRequestException;
import org.rootservices.authorization.openId.jwk.GetKeys;
import org.rootservices.authorization.openId.jwk.entity.RSAPublicKey;
import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.ToJsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RSAPublicKeysResource extends Resource {
    private static final Logger logger = LogManager.getLogger(RSAPublicKeysResource.class);
    public static String URL = "/api/v1/jwk/rsa(?!/)(.*)";

    private JsonTranslator translator;
    private QueryStringToMap queryStringToMap;
    private GetKeys getKeys;

    private static String PAGE_NUMBER_PARAM = "page";
    private static Integer DEFAULT_PAGE_NUMBER = 1;
    private static String BAD_REQUEST_ERROR = "page value is not a Integer";

    @Autowired
    public RSAPublicKeysResource(JsonTranslator translator, QueryStringToMap queryStringToMap, GetKeys getKeys) {
        super();
        this.translator = translator;
        this.queryStringToMap = queryStringToMap;
        this.getKeys = getKeys;
    }

    @Override
    public Response get(Request request, Response response) {
        setDefaultHeaders(response);

        Integer pageNumber;
        try {
            pageNumber = getPageNumber(request.getQueryParams());
        } catch (BadRequestException e) {
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }

        List<RSAPublicKey> keys =  getKeys.getPublicKeys(pageNumber);
        Optional<ByteArrayOutputStream> payload = Optional.empty();
        try {
            payload = Optional.of(translator.to(keys));
        } catch (ToJsonException e) {
            logger.error(e.getMessage(), e);
        }

        response.setPayload(payload);
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    private Integer getPageNumber(Map<String, List<String>> parameters) throws BadRequestException{
        Integer pageNumber = DEFAULT_PAGE_NUMBER;
        List<String> pageNumbers = parameters.get(PAGE_NUMBER_PARAM);
        if (pageNumbers != null && pageNumbers.size() == 1) {
            try {
                pageNumber = Integer.parseInt(pageNumbers.get(0));
            } catch (NumberFormatException nfe) {
                throw new BadRequestException(BAD_REQUEST_ERROR);
            }
        }
        return pageNumber;
    }

    protected void setDefaultHeaders(Response response) {
        Map<String, String> headers = new HashMap<>();

        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), "application/json;charset=UTF-8");
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_STORE.getValue());
        headers.put(Header.PRAGMA.getValue(), HeaderValue.NO_CACHE.getValue());

        response.getHeaders().putAll(headers);
    }
}
