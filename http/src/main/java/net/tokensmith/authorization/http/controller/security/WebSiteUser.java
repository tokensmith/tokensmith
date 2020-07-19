package net.tokensmith.authorization.http.controller.security;


import net.tokensmith.otter.controller.entity.DefaultUser;

import java.time.OffsetDateTime;
import java.util.UUID;

public class WebSiteUser extends DefaultUser {
    private UUID id;
    private String email;
    private Boolean emailVerified;
    private OffsetDateTime createdAt;


    public WebSiteUser(UUID id, String email, Boolean emailVerified, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private UUID id;
        private String email;
        private Boolean emailVerified;
        private OffsetDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder emailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public WebSiteUser build() {
            return new WebSiteUser(id, email, emailVerified, createdAt);
        }
    }
}
