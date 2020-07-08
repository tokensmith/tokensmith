package helpers;

import io.netty.handler.codec.http.cookie.Cookie;
import org.asynchttpclient.cookie.CookieStore;
import org.asynchttpclient.uri.Uri;

import java.util.List;
import java.util.function.Predicate;

public class NoOpCookieStore implements CookieStore {
    @Override
    public void add(Uri uri, Cookie cookie) {

    }

    @Override
    public List<Cookie> get(Uri uri) {
        return null;
    }

    @Override
    public List<Cookie> getAll() {
        return null;
    }

    @Override
    public boolean remove(Predicate<Cookie> predicate) {
        return false;
    }

    @Override
    public boolean clear() {
        return false;
    }

    @Override
    public void evictExpired() {

    }

    @Override
    public int incrementAndGet() {
        return 0;
    }

    @Override
    public int decrementAndGet() {
        return 0;
    }

    @Override
    public int count() {
        return 0;
    }
}
