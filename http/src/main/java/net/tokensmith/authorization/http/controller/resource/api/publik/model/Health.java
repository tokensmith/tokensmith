package net.tokensmith.authorization.http.controller.resource.api.publik.model;


import net.tokensmith.otter.translatable.Translatable;

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
