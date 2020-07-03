package net.tokensmith.authorization.http.controller.resource.html.authorization.claim;


import net.tokensmith.jwt.entity.jwt.Claims;

public class RedirectClaim extends Claims {
    private String redirect;
    private Boolean done;

    public RedirectClaim() {
    }

    public RedirectClaim(String redirect, Boolean done) {
        this.redirect = redirect;
        this.done = done;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}
