package org.rootservices.authorization.http.response;

/**
 * Created by tommackenzie on 7/8/15.
 */
public class Error {
    private String error;
    private String description;

    public Error() {
    }

    public Error(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
