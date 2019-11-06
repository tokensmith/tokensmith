package net.tokensmith.authorization.parse.validator;

/**
 * Created by tommackenzie on 3/31/17.
 */
public enum SupportedTypes {
    UUID ("java.util.UUID");

    private String type;

    SupportedTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
