package org.rootservices.authorization.http.controller.resource.api.model;

import org.rootservices.otter.translatable.Translatable;

public class Health implements Translatable {
    public enum Status {
        UP
    }

    public Status status;

    public Health(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
