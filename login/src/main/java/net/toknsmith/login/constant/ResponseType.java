package net.toknsmith.login.constant;

public enum ResponseType {
    CODE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
