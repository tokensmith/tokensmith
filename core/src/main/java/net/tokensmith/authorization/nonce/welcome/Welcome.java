package net.tokensmith.authorization.nonce.welcome;

import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.NotFoundException;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.NonceRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.authorization.security.entity.NonceClaim;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Welcome {
    private HashToken hashToken;
    private NonceRepository nonceRepository;
    private ResourceOwnerRepository resourceOwnerRepository;

    @Autowired
    public Welcome(HashToken hashToken, NonceRepository nonceRepository, ResourceOwnerRepository resourceOwnerRepository) {
        this.hashToken = hashToken;
        this.nonceRepository = nonceRepository;
        this.resourceOwnerRepository = resourceOwnerRepository;
    }

    public void markEmailVerified(String jwt) throws BadRequestException, NotFoundException {
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken<NonceClaim> jsonWebToken;
        try {
            jsonWebToken = jwtSerde.stringToJwt(jwt, NonceClaim.class);
        } catch (JsonToJwtException e) {
            throw new BadRequestException("Could not marshal to a jwt", e);
        } catch (InvalidJWT e) {
            throw new BadRequestException("Input was not a JWT", e);
        }

        NonceClaim nonceClaim = jsonWebToken.getClaims();

        String hashedNonce = hashToken.run(nonceClaim.getNonce());

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
