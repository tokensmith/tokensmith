package org.rootservices.authorization.nonce.welcome;

import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
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
public class Welcome {
    private HashTextStaticSalt hashTextStaticSalt;
    private NonceRepository nonceRepository;
    private ResourceOwnerRepository resourceOwnerRepository;

    @Autowired
    public Welcome(HashTextStaticSalt hashTextStaticSalt, NonceRepository nonceRepository, ResourceOwnerRepository resourceOwnerRepository) {
        this.hashTextStaticSalt = hashTextStaticSalt;
        this.nonceRepository = nonceRepository;
        this.resourceOwnerRepository = resourceOwnerRepository;
    }

    public void markEmailVerified(String jwt) throws BadRequestException, NotFoundException {
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken jsonWebToken;
        try {
            jsonWebToken = jwtSerde.stringToJwt(jwt, NonceClaim.class);
        } catch (JsonToJwtException e) {
            throw new BadRequestException("Could not marshal to a jwt", e);
        } catch (InvalidJWT e) {
            throw new BadRequestException("Input was not a JWT", e);
        }

        NonceClaim nonceClaim = (NonceClaim) jsonWebToken.getClaims();

        String hashedNonce = hashTextStaticSalt.run(nonceClaim.getNonce());

        Nonce nonce;
        try {
            nonce = nonceRepository.getByTypeAndNonce(NonceName.WELCOME, hashedNonce);
        } catch (RecordNotFoundException e) {
            throw new NotFoundException("Nonce not found", e);
        }

        resourceOwnerRepository.setEmailVerified(nonce.getResourceOwner().getId());
        nonceRepository.setSpent(nonce.getId());
        nonceRepository.revokeUnSpent(nonce.getNonceType().getName(), nonce.getResourceOwner().getId());
    }
}
