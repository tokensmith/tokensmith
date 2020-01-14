package net.toknsmith.login.http;

import net.toknsmith.login.constant.Delimitter;
import net.toknsmith.login.exception.URLException;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class URLBuilder {
    private static String ASSIGNMENT="=";
    private static String BEGIN_QS="?";
    private static String DELIMITTER="&";
    private static String URL_ERROR_MSG = "Could not construct URL";
    private static String URL_KEY_ERROR_MSG = "Could not encode url parameter. Key: %s, Value: %s";
    private URI baseURL;
    private Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();

    public URLBuilder baseUrl(URI baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public URLBuilder param(String key, String value) {
        if(this.params.containsKey(key) ){
            this.params.get(key).add(value);
        } else {
            this.params.put(key, Collections.singletonList(value));
        }
        return this;
    }

    public URLBuilder params(String key, List<String> values) {
        if(this.params.containsKey(key) ){
            this.params.get(key).addAll(values);
        } else {
            this.params.put(key, values);
        }
        return this;
    }

    public URLBuilder paramsWhiteSpaceDelimitted(String key, List<String> values) {
        String value = values.stream().collect(Collectors.joining(Delimitter.SPACE.toString()));
        if(this.params.containsKey(key) ){
            String existingValues = this.params.get(key).get(0);
            String paramValue = String.join(Delimitter.SPACE.toString(), existingValues, value);
            this.params.put(key, Collections.singletonList(paramValue));
        } else {
            this.params.put(key, Collections.singletonList(value));
        }
        return this;
    }

    public URL build() throws URLException {

        String urlWithParams = baseURL + BEGIN_QS + queryString().toString();

        URL url;
        try {
            url =  new URL(urlWithParams);
        } catch (MalformedURLException e) {
            throw new URLException(URL_ERROR_MSG, e);
        }
        return url;
    }

    protected StringBuilder queryString() throws URLException {
        StringBuilder queryString = new StringBuilder();
        for(Map.Entry<String, List<String>> entry: params.entrySet()) {

            if (queryString.length() > 0) {
                // is not the first param key.
                queryString.append(DELIMITTER);
            }

            List<String> values = entry.getValue();

            for(int j=0; j < values.size(); j++) {
                try {
                    queryString.append(entry.getKey())
                            .append(ASSIGNMENT)
                            .append(URLEncoder.encode(values.get(j), StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    throw new URLException(String.format(URL_KEY_ERROR_MSG, entry.getKey(), entry.getValue()), e);
                }
                if (j < values.size() - 1) {
                    // has more values
                    queryString.append(DELIMITTER);
                }
            }
        }
        return queryString;
    }
}
