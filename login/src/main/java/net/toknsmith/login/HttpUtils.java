package net.toknsmith.login;

import net.toknsmith.login.endpoint.UserEndpoint;
import net.toknsmith.login.http.Header;
import net.toknsmith.login.http.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.*;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);
    private final String correlationIdField;

    public HttpUtils(String correlationIdField) {
        this.correlationIdField = correlationIdField;
    }

    public Optional<StatusCode> toStatusCode(int from) {
        return Arrays.stream(StatusCode.values()).filter(f -> f.getCode() == from).findFirst();
    }

    /**
     * This is needed until the bug below is fixed.
     * https://stackoverflow.com/questions/53379087/wrapping-bodysubscriberinputstream-in-gzipinputstream-leads-to-hang
     *
     * @param response an instance of HttpResponse<InputStream>
     * @return InputStream that is decompressed.
     */
    public InputStream processResponse(HttpResponse<InputStream> response) {
        Optional<InputStream> decompressed = decompress(response);

        InputStream body = null;
        if (decompressed.isEmpty()) {
            body = response.body();
        } else {
            body = decompressed.get();
        }
        return body;
    }

    protected Optional<InputStream> decompress(HttpResponse<InputStream> from) {
        Optional<InputStream> to = Optional.empty();
        List<String> encodings = from.headers().map().get(Header.CONTENT_ENCODING.toString());
        if (encodings != null && encodings.contains("gzip")) {
            try {
                to = Optional.of(new GZIPInputStream(from.body()));
            } catch (IOException e) {
                LOGGER.error("unable to decompress response");
            }
        }
        return to;
    }

    /**
     * Translates a Map that contains form elements to a String in the content-type, application/x-www-form-urlencoded
     *
     * @param from Map of form elements
     * @return application/x-www-form-urlencoded request body
     */
    public String toBody(Map<String, List<String>> from) {

        StringBuilder to = new StringBuilder();
        int i = 0;
        for(Map.Entry<String, List<String>> element: from.entrySet()) {

            StringBuilder values = new StringBuilder();
            for (int j = 0; j< element.getValue().size(); j++) {
                if (j>0)
                    values.append(" ");

                values.append(element.getValue().get(j));
            }
            to.append(element.getKey())
                    .append("=")
                    .append(values.toString());

            if (i < from.size()-1) {
                to.append("&");
            }
            i++;

        }
        return to.toString();
    }

    /**
     * Translates an InputStream to it's String representation in UTF-8
     *
     * @param from InputStream
     * @return UTF-8 encoded String
     */
    public String to(InputStream from) {
        StringBuilder toBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (from, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                toBuilder.append((char) c);
            }
        } catch (IOException e) {
            LOGGER.error("failed to convert input stream to string");
        }
        return toBuilder.toString();
    }

    public String getCorrelationId() {
        String correlationId = MDC.get(correlationIdField);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            MDC.put(correlationIdField, correlationId);
            LOGGER.debug("Adding correlation id to MDC");
        }
        return correlationId;
    }
}
