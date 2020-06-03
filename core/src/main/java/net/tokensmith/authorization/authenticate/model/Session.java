package net.tokensmith.authorization.authenticate.model;

public class Session {
    private String token;
    private Long issuedAt;

    public Session(String token, Long issuedAt) {
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
}
