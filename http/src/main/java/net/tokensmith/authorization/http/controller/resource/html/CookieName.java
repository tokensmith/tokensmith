package net.tokensmith.authorization.http.controller.resource.html;

public enum CookieName {
    REDIRECT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
