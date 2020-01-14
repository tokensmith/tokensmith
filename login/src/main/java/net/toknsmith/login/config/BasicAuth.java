package net.toknsmith.login.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuth {
    private static String BASIC = "Basic %s";
    private Base64.Encoder base64Encoder;

    public BasicAuth(Base64.Encoder base64Encoder) {
        this.base64Encoder = base64Encoder;
    }

    public String encodeCredentials(String username, String password) {

        String plainText = username + ":" + password;
        String encodedCredentials = new String(
                base64Encoder.encode(plainText.getBytes()),
                StandardCharsets.UTF_8
        );

        return String.format(BASIC, encodedCredentials);
    }
}
