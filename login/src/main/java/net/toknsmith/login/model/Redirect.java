package net.toknsmith.login.model;


import java.net.URL;

public class Redirect {
    private URL location;
    private String nonce;
    private String state;

    public Redirect(URL location, String nonce, String state) {
        this.location = location;
        this.nonce = nonce;
        this.state = state;
    }

    public URL getLocation() {
        return location;
    }

    public String getNonce() {
        return nonce;
    }

    public String getState() {
        return state;
    }
}
