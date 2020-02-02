package net.tokensmith.authorization.http.controller.resource.api.site.model;

import java.util.UUID;

public class Name {
    private UUID id;
    private UUID profileId;
    private String name;

    public Name() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
