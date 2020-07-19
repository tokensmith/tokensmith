package net.toknsmith.login.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;
import net.toknsmith.login.exception.TranslateException;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

/**
 * Used to translate non 200 response bodies for, /token
 */
public class ErrorResponseTranslator {
    private static String RESPONSE_MSG = "Unable to serialize the response from the Identity Server";
    private ObjectMapper objectMapper;

    public ErrorResponseTranslator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TokenErrorResponse to(HttpResponse<InputStream> response, InputStream body) throws TranslateException {
        TokenErrorResponse error;
        try {
            error = objectMapper.readValue(body, TokenErrorResponse.class);
        } catch (IOException e) {
            throw new TranslateException(RESPONSE_MSG, e);
        }
        return error;
    }
}
