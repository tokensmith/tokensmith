package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity;

import java.util.List;

/**
 * Created by tommackenzie on 9/7/16.
 */
public class InputParams {
    private String userName;
    private String password;
    private List<String> clientIds;
    private List<String> responseTypes;
    private List<String> redirectUris;
    private List<String> scopes;
    private List<String> states;
    private List<String> nonces;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getClientIds() {
        return clientIds;
    }

    public void setClientIds(List<String> clientIds) {
        this.clientIds = clientIds;
    }

    public List<String> getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(List<String> responseTypes) {
        this.responseTypes = responseTypes;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public List<String> getNonces() {
        return nonces;
    }

    public void setNonces(List<String> nonces) {
        this.nonces = nonces;
    }
}