package net.tokensmith.authorization.nonce;

import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.NotFoundException;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.NonceRepository;
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
public class SpendNonce {
    private static String MARSHAL_MSG = "Could not marshal to a jwt";
    private static String NOT_JWT_MSG = "Input was not a JWT";
    private static String NOT_FOUND_MSG = "Nonce not found";
    private HashToken hashToken;
    private NonceRepository nonceRepository;

    @Autowired
    public SpendNonce(HashToken hashToken, NonceRepository nonceRepository) {
        this.hashToken = hashToken;
        this.nonceRepository = nonceRepository;
    }

    public Nonce spend(String jwt, NonceName nonceName) throws BadRequestException, NotFoundException {

        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken<NonceClaim> jsonWebToken;
        try {
            jsonWebToken = jwtSerde.stringToJwt(jwt, NonceClaim.class);
        } catch (JsonToJwtException e) {
            throw new BadRequestException(MARSHAL_MSG, e);
        } catch (InvalidJWT e) {
            throw new BadRequestException(NOT_JWT_MSG, e);
        }

        NonceClaim nonceClaim = jsonWebToken.getClaims();

        String hashedNonce = hashToken.run(nonceClaim.getNonce());

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
