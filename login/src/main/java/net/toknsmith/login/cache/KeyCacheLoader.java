package net.toknsmith.login.cache;


import com.github.benmanes.caffeine.cache.CacheLoader;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.toknsmith.login.endpoint.KeyEndpoint;

import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.exception.URLException;
import net.toknsmith.login.exception.http.api.ClientException;
import net.toknsmith.login.exception.http.api.ServerException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeyCacheLoader implements CacheLoader<String, RSAPublicKey> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyCacheLoader.class);
    private KeyEndpoint keyEndpoint;

    public KeyCacheLoader(KeyEndpoint keyEndpoint) {
        this.keyEndpoint = keyEndpoint;
    }

    @Nullable
    @Override
    public RSAPublicKey load(@NonNull String key) {
        LOGGER.debug("attempting to acquire key: {}", key);
        RSAPublicKey publicKey;

        try {
            publicKey = keyEndpoint.getKey(key);
        } catch (URLException e) {
            throw new KeyException("Could not create URL to get key", e);
        } catch (CommException e) {
            throw new KeyException("Could not communicate with ID Server", e);
        } catch (TranslateException e) {
            throw new KeyException("Could not translate the response from the ID Server", e);
        } catch (ClientException e) {
            throw new KeyException("ID Server returned 4XX", e);
        } catch (ServerException e) {
            throw new KeyException("ID Server returned 5XX", e);
        }
        return publicKey;
    }
}
