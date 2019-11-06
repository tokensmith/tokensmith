package net.tokensmith.repository.entity;

public enum NonceName {
    WELCOME, RESET_PASSWORD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
