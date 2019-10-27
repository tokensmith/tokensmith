package net.tokensmith.authorization.parse.validator;

/**
 * Created by tommackenzie on 3/31/17.
 */
public enum RawType {
    LIST ("java.util.List"),
    OPTIONAL ("java.util.Optional");

    private String typeName;

    RawType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
