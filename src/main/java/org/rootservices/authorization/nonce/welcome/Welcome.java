package org.rootservices.authorization.nonce.welcome;

import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.authorization.security.entity.NonceClaim;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.serializer.JWTSerializer;
import org.rootservices.jwt.serializer.exception.JsonToJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Welcome {
    private static String WELCOME_TYPE = "welcome";
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
        AppFactory appFactory = new AppFactory();
        JWTSerializer jwtSerializer = appFactory.jwtSerializer();

        JsonWebToken jsonWebToken;
        try {
            jsonWebToken = jwtSerializer.stringToJwt(jwt, NonceClaim.class);
        } catch (JsonToJwtException e) {
            throw new BadRequestException("Could not marshal to a jwt", e);
        } catch(ArrayIndexOutOfBoundsException e) {
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
