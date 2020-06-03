package net.tokensmith.authorization.http.controller.security;


import net.tokensmith.otter.controller.entity.DefaultSession;

import java.util.Objects;

public class WebSiteSession extends DefaultSession {
    private String token;
    private Long issuedAt;

    // copy constructor for otter
    public WebSiteSession(WebSiteSession from) {
        this.token = from.getToken();
        this.issuedAt = from.getIssuedAt();
    }

    public WebSiteSession() {
    }

    public WebSiteSession(String token, Long issuedAt) {
        this.token = token;
        this.issuedAt = issuedAt;
    }

    public WebSiteSession(DefaultSession defaultSession, String token, Long issuedAt) {
        super(defaultSession);
        this.token = token;
        this.issuedAt = issuedAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Long issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSiteSession that = (WebSiteSession) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(issuedAt, that.issuedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, issuedAt);
    }
}
