package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.jwt.translator.PemToRSAKeyPair;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 2/1/16.
 */
public class PrivateKeyMapperTest {

    @Test
    public void testInsert() throws Exception {

        // TODO: left off here - i should create a generator for a keypair
        // i shoudl rename private key to "rsa private key" everywhere.

    }
}