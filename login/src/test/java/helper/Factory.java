package helper;


import com.github.tomakehurst.wiremock.http.HttpHeader;
import helper.fake.FakeHttpResponse;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.entity.jwt.header.Header;
import net.toknsmith.login.endpoint.entity.response.openid.OpenIdToken;
import net.toknsmith.login.endpoint.entity.response.openid.TokenType;
import net.toknsmith.login.endpoint.entity.response.api.key.RSAPublicKey;
import net.tokensmith.jwt.entity.jwk.KeyType;
import net.tokensmith.jwt.entity.jwk.RSAKeyPair;
import net.tokensmith.jwt.entity.jwk.Use;
import net.toknsmith.login.http.StatusCode;
import net.toknsmith.login.endpoint.entity.response.api.key.KeyUse;



import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiPredicate;

import static org.mockito.Mockito.mock;

public class Factory {
    /*
     * This RSA key pair comes from, https://tools.ietf.org/html/rfc7515#appendix-A.2
     */
    public static RSAKeyPair makeRSAKeyPair() {
        return new RSAKeyPair(
                Optional.empty(),
                KeyType.RSA,
                Use.SIGNATURE,
                toBigInt("ofgWCuLjybRlzo0tZWJjNiuSfb4p4fAkd_wWJcyQoTbji9k0l8W26mPddxHmfHQp-Vaw-4qPCJrcS2mJPMEzP1Pt0Bm4d4QlL-yRT-SFd2lZS-pCgNMsD1W_YpRPEwOWvG6b32690r2jZ47soMZo9wGzjb_7OMg0LOL-bSf63kpaSHSXndS5z5rexMdbBYUsLA9e-KXBdQOS-UTo7WTBEMa2R2CapHg665xsmtdVMTBQY4uDZlxvb3qCo5ZwKh9kG4LT6_I5IhlJH7aGhyxXFvUK-DWNmoudF8NAco9_h9iaGNj8q2ethFkMLs91kzk2PAcDTW9gb54h4FRWyuXpoQ"),
                toBigInt("AQAB"),
                toBigInt("Eq5xpGnNCivDflJsRQBXHx1hdR1k6Ulwe2JZD50LpXyWPEAeP88vLNO97IjlA7_GQ5sLKMgvfTeXZx9SE-7YwVol2NXOoAJe46sui395IW_GO-pWJ1O0BkTGoVEn2bKVRUCgu-GjBVaYLU6f3l9kJfFNS3E0QbVdxzubSu3Mkqzjkn439X0M_V51gfpRLI9JYanrC4D4qAdGcopV_0ZHHzQlBjudU2QvXt4ehNYTCBr6XCLQUShb1juUO1ZdiYoFaFQT5Tw8bGUl_x_jTj3ccPDVZFD9pIuhLhBOneufuBiB4cS98l2SR_RQyGWSeWjnczT0QU91p1DhOVRuOopznQ"),
                toBigInt("4BzEEOtIpmVdVEZNCqS7baC4crd0pqnRH_5IB3jw3bcxGn6QLvnEtfdUdiYrqBdss1l58BQ3KhooKeQTa9AB0Hw_Py5PJdTJNPY8cQn7ouZ2KKDcmnPGBY5t7yLc1QlQ5xHdwW1VhvKn-nXqhJTBgIPgtldC-KDV5z-y2XDwGUc"),
                toBigInt("uQPEfgmVtjL0Uyyx88GZFF1fOunH3-7cepKmtH4pxhtCoHqpWmT8YAmZxaewHgHAjLYsp1ZSe7zFYHj7C6ul7TjeLQeZD_YwD66t62wDmpe_HlB-TnBA-njbglfIsRLtXlnDzQkv5dTltRJ11BKBBypeeF6689rjcJIDEz9RWdc"),
                toBigInt("BwKfV3Akq5_MFZDFZCnW-wzl-CCo83WoZvnLQwCTeDv8uzluRSnm71I3QCLdhrqE2e9YkxvuxdBfpT_PI7Yz-FOKnu1R6HsJeDCjn12Sk3vmAktV2zb34MCdy7cpdTh_YVr7tss2u6vneTwrA86rZtu5Mbr1C1XsmvkxHQAdYo0"),
                toBigInt("h_96-mK1R_7glhsum81dZxjTnYynPbZpHziZjeeHcXYsXaaMwkOlODsWa7I9xXDoRwbKgB719rrmI2oKr6N3Do9U0ajaHF-NKJnwgjMd2w9cjz3_-kyNlxAr2v4IKhGNpmM5iIgOS1VZnOZ68m6_pbLBSp3nssTdlqvd0tIiTHU"),
                toBigInt("IYd7DHOhrWvxkwPQsRM2tOgrjbcrfvtQJipd-DlcxyVuuM9sQLdgjVk2oy26F0EmpScGLq2MowX7fhd_QJQ3ydy5cY7YIBi87w93IKLEdfnbJtoOPLUW0ITrJReOgo1cq9SbsxYawBgfp_gh6A5603k2-ZQwVK0JKSHuLFkuQ3U")
        );
    }

    public static RSAPublicKey serverKey() {
        RSAPublicKey serverKey = new RSAPublicKey();
        serverKey.setKeyId(UUID.randomUUID());
        serverKey.setUse(KeyUse.SIGNATURE);
        serverKey.setE(toBigInt("ofgWCuLjybRlzo0tZWJjNiuSfb4p4fAkd_wWJcyQoTbji9k0l8W26mPddxHmfHQp-Vaw-4qPCJrcS2mJPMEzP1Pt0Bm4d4QlL-yRT-SFd2lZS-pCgNMsD1W_YpRPEwOWvG6b32690r2jZ47soMZo9wGzjb_7OMg0LOL-bSf63kpaSHSXndS5z5rexMdbBYUsLA9e-KXBdQOS-UTo7WTBEMa2R2CapHg665xsmtdVMTBQY4uDZlxvb3qCo5ZwKh9kG4LT6_I5IhlJH7aGhyxXFvUK-DWNmoudF8NAco9_h9iaGNj8q2ethFkMLs91kzk2PAcDTW9gb54h4FRWyuXpoQ"));
        serverKey.setN(toBigInt("AQAB"));
        return serverKey;
    }

    public static BigInteger toBigInt(String value) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(value);
        return new BigInteger(1, decodedBytes);
    }


    public static <T> HttpResponse<T> makeFakeResponse(Map<String, List<String>> headerMap, T body, StatusCode statusCode) {
        BiPredicate<String,String> acceptAll = (x, y) -> true;
        HttpHeaders headers =  HttpHeaders.of(headerMap, acceptAll);

        HttpResponse<T> fakeHttpResponse = new FakeHttpResponse<T>(
                statusCode.getCode(),
                null,
                null,
                headers,
                null,
                null,
                HttpClient.Version.HTTP_2,
                body
        );
        return fakeHttpResponse;
    }

    public static HttpResponse<InputStream> makeFakeResponseBadRequest() {
        String json = new StringBuilder()
                .append("{\n")
                .append("  \"error\": \"invalid_request\",\n")
                .append("  \"description\": \"client_id is repeated\"\n")
                .append("}")
                .toString();

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Arrays.asList("application/json;charset=UTF-8"));
        headerMap.put("WWW-Authenticate", Arrays.asList("Basic"));
        headerMap.put("Cache-Control", Arrays.asList("no-store"));
        headerMap.put("Pragma", Arrays.asList("no-store"));

        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return makeFakeResponse(headerMap, body, StatusCode.BAD_REQUEST);
    }

    public static HttpResponse<InputStream> makeFakeResponseUnAuthorized() {

        String json = new StringBuilder()
                .append("{\n")
                .append("  \"error\": \"invalid_client\"")
                .append("}")
                .toString();

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Arrays.asList("application/json;charset=UTF-8"));
        headerMap.put("WWW-Authenticate", Arrays.asList("Basic"));
        headerMap.put("Cache-Control", Arrays.asList("no-store"));

        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return makeFakeResponse(headerMap, body, StatusCode.UNAUTHORIZED);
    }

    public static HttpResponse<InputStream> makeFakeResponseNotFound() {

        String json = new StringBuilder()
                .append("{\n")
                .append("  \"error\": \"invalid_grant\",\n")
                .append("  \"description\": \"the authorization code was already used\"\n")
                .append("}")
                .toString();

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Arrays.asList("application/json;charset=UTF-8"));
        headerMap.put("WWW-Authenticate", Arrays.asList("Basic"));
        headerMap.put("Cache-Control", Arrays.asList("no-store"));

        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return makeFakeResponse(headerMap, body, StatusCode.NOT_FOUND);
    }

    public static HttpResponse<InputStream> makeFakeResponseServerError() {

        String json = new StringBuilder()
                .append("{\n")
                .append("  \"error\": \"Unhandled Server Exception\",\n")
                .append("  \"description\": \"Unhandled Server Exception\")")
                .append("\n")
                .append("}")
                .toString();

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Arrays.asList("application/json;charset=UTF-8"));
        headerMap.put("WWW-Authenticate", Arrays.asList("Basic"));
        headerMap.put("Cache-Control", Arrays.asList("no-store"));

        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return makeFakeResponse(headerMap, body, StatusCode.SERVER_ERROR);
    }

    public static HttpResponse<InputStream> makeFakeResponseTooMany() {

        // not an actual expected payload from id server, used to test translators.
        String json = new StringBuilder()
                .append("{\n")
                .append("  \"error\": \"Too Many\",\n")
                .append("  \"description\": \"Gear down big shifter\")")
                .append("\n")
                .append("}")
                .toString();

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Arrays.asList("application/json;charset=UTF-8"));
        headerMap.put("WWW-Authenticate", Arrays.asList("Basic"));
        headerMap.put("Cache-Control", Arrays.asList("no-store"));

        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return makeFakeResponse(headerMap, body, StatusCode.TOO_MANY);
    }

    public static HttpResponse<InputStream> makeFakeResponseForApiBadRequest() {
        // TODO: need to decide payload.
        String json = new StringBuilder()
                .toString();

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Arrays.asList("application/json;charset=UTF-8"));
        headerMap.put("Cache-Control", Arrays.asList("no-store"));
        headerMap.put("Pragma", Arrays.asList("no-store"));

        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return makeFakeResponse(headerMap, body, StatusCode.BAD_REQUEST);
    }

    public static HttpResponse<InputStream> makeFakeResponseForApiServerError() {
        // TODO: need to decide payload.
        String json = new StringBuilder()
                .toString();

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Arrays.asList("application/json;charset=UTF-8"));
        headerMap.put("Cache-Control", Arrays.asList("no-store"));
        headerMap.put("Pragma", Arrays.asList("no-store"));

        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return makeFakeResponse(headerMap, body, StatusCode.SERVER_ERROR);
    }


    public static Map<String, String> secrets(String baseUrl, Integer port) {

        Map<String, String> secrets = new HashMap<>();
        secrets.put("CLIENT_ID", "123456789");
        secrets.put("CLIENT_USER_NAME", "username");
        secrets.put("CLIENT_PASSWORD", "password");
        secrets.put("LOGIN_URL", String.format(baseUrl, port, "api/v1/token"));
        secrets.put("TOKEN_URL", String.format(baseUrl, port, "api/v1/token"));
        secrets.put("USER_INFO_URL", String.format(baseUrl, port, "api/v1/userinfo"));
        secrets.put("AUTHORIZATION_URL", String.format(baseUrl, port, "authorization"));
        secrets.put("PUBLIC_KEY_URL", String.format(baseUrl, port, "api/v1/jwk/rsa/%s"));
        secrets.put("CORRELATION_ID_FIELD", "correlation-id");
        return secrets;
    }

    public static List<String> scopes() {
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        scopes.add("openid");
        return scopes;
    }

    public static RSAPublicKey rsaPublicKey() {
        return new RSAPublicKey(
                UUID.fromString("1b469919-5605-4874-8dd2-44f9a3afc490"),
                KeyUse.SIGNATURE,
                new BigInteger("25460493613279424441611949918389782566632053794560283854781116188091469278199435629088914951946592605013194012578766411350155252425813300678928516421246948087642866742961902379972059935922399442372168504558597434070750118159850289265459003868309378010135522239311592583331397279437991288408885447682375320372624669096837891670516082441310689922711113053871205711196784897697206334704065342469867244707792552601440201949082540409347520555821428265262800979456493790020869023122219993847650425369096972769084082194543500824938836959901147903007980583137254619526913512066068570207992804528579668787876657494219488902713"),
                new BigInteger("65537")
        );
    }

    public static net.tokensmith.jwt.entity.jwk.RSAPublicKey rsaPublicKeyTranslated() {
        return new net.tokensmith.jwt.entity.jwk.RSAPublicKey(
                Optional.of(UUID.fromString("1b469919-5605-4874-8dd2-44f9a3afc490").toString()),
                KeyType.RSA,
                Use.SIGNATURE,
                new BigInteger("25460493613279424441611949918389782566632053794560283854781116188091469278199435629088914951946592605013194012578766411350155252425813300678928516421246948087642866742961902379972059935922399442372168504558597434070750118159850289265459003868309378010135522239311592583331397279437991288408885447682375320372624669096837891670516082441310689922711113053871205711196784897697206334704065342469867244707792552601440201949082540409347520555821428265262800979456493790020869023122219993847650425369096972769084082194543500824938836959901147903007980583137254619526913512066068570207992804528579668787876657494219488902713"),
                new BigInteger("65537")
        );
    }

    public static OpenIdToken okIdToken() {
        return new OpenIdToken(
                "o416b7kscflhcmfig6jg6kqvp0na7u0u",
                "epgmgqi754lsaqfvbakqi26b32hk2i4r",
                3600L,
                TokenType.BEARER,
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjFiNDY5OTE5LTU2MDUtNDg3NC04ZGQyLTQ0ZjlhM2FmYzQ5MCJ9.eyJpc3MiOiJodHRwczovL3Nzby50b2tlbnNtaXRoLm5ldCIsImF1ZCI6WyI0Njc5MTE2Zi1kNzIwLTRlMGEtOTA2OC1mN2Q0N2YzZTRmYzciXSwiZXhwIjoxNTc4ODQ2NDE0LCJpYXQiOjE1Nzg4NDI4MTQsImVtYWlsIjoidGVzdC1kZTY0NzRlZi01Y2M1LTQzNTMtOWE1ZC00NzhkNGI1YjYxNmJAdG9rZW5zbWl0aC5uZXQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImF1dGhfdGltZSI6MTU3ODg0MjgxNCwibm9uY2UiOiJub25jZS0xMjMifQ.cCutPehYNda2Q75fg83EecfqRUKkvDq0wSmynxsP6_IAavxpz2uipfQhijIKsf65mMiTZzInToE3n4ZcdutwQf-RUOgToWl8EQPDs4MWTYGbURsAJ5JmURoGqsVRThy9_8pwNmRJilB-8tfkRmFisTqeEgcBxCq7g5YA5dhbZunhWcQJSXwhSNzQb86FS9kQTPB6VzLno7VvPKqBveho_ieYb707Poo0BCnaSuVc9xTncbgbtgzxL_wPuTsdBWVdRc-UkV_dk-YWnvoOiY64U-23UTHuyTBv7_LHyF2M0FLlhhlGAcy1-QAfOR5SD8EPl9_bmp4Lz_HtOJKCKySF6g"
        );
    }

    public static OpenIdToken idTokenWithBadSignature() {
        return new OpenIdToken(
                "o416b7kscflhcmfig6jg6kqvp0na7u0u",
                "epgmgqi754lsaqfvbakqi26b32hk2i4r",
                3600L,
                TokenType.BEARER,
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjFiNDY5OTE5LTU2MDUtNDg3NC04ZGQyLTQ0ZjlhM2FmYzQ5MCJ9.eyJpc3MiOiJodHRwczovL3Nzby50b2tlbnNtaXRoLm5ldCIsImF1ZCI6WyI0Njc5MTE2Zi1kNzIwLTRlMGEtOTA2OC1mN2Q0N2YzZTRmYzciXSwiZXhwIjoxNTc4ODQ2NDE0LCJpYXQiOjE1Nzg4NDI4MTQsImVtYWlsIjoidGVzdC1kZTY0NzRlZi01Y2M1LTQzNTMtOWE1ZC00NzhkNGI1YjYxNmJAdG9rZW5zbWl0aC5uZXQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImF1dGhfdGltZSI6MTU3ODg0MjgxNCwibm9uY2UiOiJub25jZS0xMjMifQ.cCutPehYNda2Q75fg83EecfqRUKkvDq0wSmynxsP6_IAavxpz2uipfQhijIKsf65mMiTZzInToE3n4ZcdutwQf-RUOgToWl8EQPDs4MWTYGbURsAJ5JmURoGqsVRThy9_8pwNmRJilB-8tfkRmFisTqeEgcBxCq7g5YA5dhbZunhWcQJSXwhSNzQb86FS9kQTPB6VzLno7VvPKqBveho_ieYb707Poo0BCnaSuVc9xTncbgbtgzxL_wPuTsdBWVdRc-UkV_dk-YWnvoOiY64U-23UTHuyTBv7_LHyF2M0FLlhhlGAcy1-QAfOR5SD8EPl9_bmp4Lz_HtOJKCKySF"
        );
    }

    public static String okUserInfoResponseBody() {
        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjFiNDY5OTE5LTU2MDUtNDg3NC04ZGQyLTQ0ZjlhM2FmYzQ5MCJ9.eyJpc3MiOiJodHRwczovL3Nzby50b2tlbnNtaXRoLm5ldCIsImF1ZCI6WyI0Njc5MTE2Zi1kNzIwLTRlMGEtOTA2OC1mN2Q0N2YzZTRmYzciXSwiZXhwIjoxNTc4ODQ2NDE0LCJpYXQiOjE1Nzg4NDI4MTQsImVtYWlsIjoidGVzdC1kZTY0NzRlZi01Y2M1LTQzNTMtOWE1ZC00NzhkNGI1YjYxNmJAdG9rZW5zbWl0aC5uZXQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImF1dGhfdGltZSI6MTU3ODg0MjgxNCwibm9uY2UiOiJub25jZS0xMjMifQ.cCutPehYNda2Q75fg83EecfqRUKkvDq0wSmynxsP6_IAavxpz2uipfQhijIKsf65mMiTZzInToE3n4ZcdutwQf-RUOgToWl8EQPDs4MWTYGbURsAJ5JmURoGqsVRThy9_8pwNmRJilB-8tfkRmFisTqeEgcBxCq7g5YA5dhbZunhWcQJSXwhSNzQb86FS9kQTPB6VzLno7VvPKqBveho_ieYb707Poo0BCnaSuVc9xTncbgbtgzxL_wPuTsdBWVdRc-UkV_dk-YWnvoOiY64U-23UTHuyTBv7_LHyF2M0FLlhhlGAcy1-QAfOR5SD8EPl9_bmp4Lz_HtOJKCKySF6g";
    }

    public static com.github.tomakehurst.wiremock.http.HttpHeaders okTokenResponseHeaders() {
        HttpHeader date = new HttpHeader("Date", "Thu, 28 Nov 2019 15:01:36 GMT");
        HttpHeader cacheControl = new HttpHeader("Cache-Control", "no-store");
        HttpHeader pragma = new HttpHeader("Pragma", "no-cache");
        HttpHeader contentType = new HttpHeader("Content-Type", "application/json; charset=UTF-8;");
        HttpHeader contentLength = new HttpHeader("Content-Length", "906");

        com.github.tomakehurst.wiremock.http.HttpHeaders headers = new com.github.tomakehurst.wiremock.http.HttpHeaders(date, cacheControl, pragma, contentType, contentLength);
        return headers;
    }

    public static JsonWebToken idToken(String keyId) {
        JsonWebToken idToken = new JsonWebToken();
        Header jwtHeader = new Header();
        jwtHeader.setKeyId(Optional.of(keyId));
        idToken.setHeader(jwtHeader);

        return idToken;
    }
}
