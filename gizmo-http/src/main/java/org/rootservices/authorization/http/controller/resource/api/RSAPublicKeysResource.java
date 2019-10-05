package org.rootservices.authorization.http.controller.resource.api;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.http.controller.exception.BadRequestException;
import org.rootservices.authorization.http.controller.security.APIUser;
import org.rootservices.authorization.openId.jwk.GetKeys;
import org.rootservices.authorization.openId.jwk.entity.RSAPublicKey;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RSAPublicKeysResource extends RestResource<APIUser, RSAPublicKey[]> {
    private static final Logger LOGGER = LogManager.getLogger(RSAPublicKeysResource.class);
    public static String URL = "/api/v1/jwk/rsa(?!/)(.*)";

    private GetKeys getKeys;

    private static String PAGE_NUMBER_PARAM = "page";
    private static Integer DEFAULT_PAGE_NUMBER = 1;
    private static String BAD_REQUEST_ERROR = "page value is not a Integer";

    @Autowired
    public RSAPublicKeysResource(GetKeys getKeys) {
        super();
        this.getKeys = getKeys;
    }

    @Override
    public RestResponse<RSAPublicKey[]> get(RestRequest<APIUser, RSAPublicKey[]> request, RestResponse<RSAPublicKey[]> response) {
        setDefaultHeaders(response);

        Integer pageNumber;
        try {
            pageNumber = getPageNumber(request.getQueryParams());
        } catch (BadRequestException e) {
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }

        RSAPublicKey[] keys = (RSAPublicKey[]) getKeys.getPublicKeys(pageNumber).toArray();
        Optional<RSAPublicKey[]> optKeys =  Optional.of(keys);

        response.setPayload(optKeys);
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

    protected void setDefaultHeaders(RestResponse<RSAPublicKey[]> response) {
        Map<String, String> headers = new HashMap<>();

        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), "application/json;charset=UTF-8");
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_STORE.getValue());
        headers.put(Header.PRAGMA.getValue(), HeaderValue.NO_CACHE.getValue());

        response.getHeaders().putAll(headers);
    }
}
