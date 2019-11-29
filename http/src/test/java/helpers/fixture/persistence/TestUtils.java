package helpers.fixture.persistence;

import com.ning.http.client.Param;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import net.tokensmith.repository.entity.RSAPrivateKey;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class TestUtils {

    public void logRequestResponse(String fileName, Request request, Response response, RSAPrivateKey key) {

        // request
        StringBuilder log = new StringBuilder()
                .append(request.getMethod()).append(" ")
                .append(request.getUri()).append("\n");

        for(Map.Entry<String, List<String>> header: request.getHeaders()) {
            log.append(String.format("%s: %s\n", header.getKey(), header.getValue()));
        }

        String requestBody = request.getStringData();
        if (requestBody != null)
            log.append(String.format("%s\n\n",request.getStringData()));

        List<Param> form = request.getFormParams();
        if (form.size() != 0)
            log.append("\n");

        for(Param formItem: form){
            log.append(String.format("%s=%s\n", formItem.getName(), formItem.getValue()));
        }

        // response
        log.append(String.format("\n%s\n", response.getStatusCode()));

        for(Map.Entry<String, List<String>> header: response.getHeaders()) {
            log.append(String.format("%s: %s\n", header.getKey(), header.getValue()));
        }

        try {
            log.append(String.format("%s\n\n", response.getResponseBody()));
        } catch (Exception e) {

        }

        // key used to sign the response.
        log.append(
            String.format("\n\npublic key:\n  id: %s,\n  modulus %s,\n  publicExponent: %s",
            key.getId(), key.getModulus(), key.getPublicExponent())
        );

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(log.toString());
            writer.close();
        } catch (Exception e) {}
    }
}
