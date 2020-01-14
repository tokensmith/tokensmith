package net.toknsmith.login.constant;

public enum Delimitter {
    SPACE (" ");

    private String value;

    Delimitter(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
