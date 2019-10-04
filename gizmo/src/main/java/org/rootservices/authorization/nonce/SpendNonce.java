package org.rootservices.authorization.nonce;

import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.authorization.security.entity.NonceClaim;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.exception.InvalidJWT;
import org.rootservices.jwt.serialization.JwtSerde;
import org.rootservices.jwt.serialization.exception.JsonToJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpendNonce {
    private static String MARSHAL_MSG = "Could not marshal to a jwt";
    private static String NOT_JWT_MSG = "Input was not a JWT";
    private static String NOT_FOUND_MSG = "Nonce not found";
    private HashTextStaticSalt hashTextStaticSalt;
    private NonceRepository nonceRepository;

    @Autowired
    public SpendNonce(HashTextStaticSalt hashTextStaticSalt, NonceRepository nonceRepository) {
        this.hashTextStaticSalt = hashTextStaticSalt;
        this.nonceRepository = nonceRepository;
    }

    public Nonce spend(String jwt, NonceName nonceName) throws BadRequestException, NotFoundException {

        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken jsonWebToken;
        try {
            jsonWebToken = jwtSerde.stringToJwt(jwt, NonceClaim.class);
        } catch (JsonToJwtException e) {
            throw new BadRequestException(MARSHAL_MSG, e);
        } catch (InvalidJWT e) {
            throw new BadRequestException(NOT_JWT_MSG, e);
        }

        NonceClaim nonceClaim = (NonceClaim) jsonWebToken.getClaims();

        String hashedNonce = hashTextStaticSalt.run(nonceClaim.getNonce());

        Nonce nonce;
        try {
            nonce = nonceRepository.getByTypeAndNonce(nonceName, hashedNonce);
        } catch (RecordNotFoundException e) {
            throw new NotFoundException(NOT_FOUND_MSG, e);
        }

        nonceRepository.setSpent(nonce.getId());
        nonceRepository.revokeUnSpent(nonce.getNonceType().getName(), nonce.getResourceOwner().getId());

        return nonce;
    }
}
