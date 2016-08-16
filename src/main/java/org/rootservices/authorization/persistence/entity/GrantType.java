package org.rootservices.authorization.persistence.entity;

/**
 * Created by tommackenzie on 4/27/16.
 */
public enum GrantType {
    AUTHORIZATION_CODE,
    TOKEN
    // TOOD: pt-125976777 should this also contain other response types such as id_token?
}
