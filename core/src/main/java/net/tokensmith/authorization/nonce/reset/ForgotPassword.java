package net.tokensmith.authorization.nonce.reset;

import net.tokensmith.authorization.nonce.exception.JwtException;
import net.tokensmith.authorization.security.entity.NonceClaim;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.NotFoundException;
import net.tokensmith.authorization.nonce.SpendNonce;
import net.tokensmith.authorization.nonce.InsertNonce;
import net.tokensmith.authorization.nonce.message.MessageKey;
import net.tokensmith.authorization.nonce.message.MessageType;
import net.tokensmith.authorization.nonce.message.Topic;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.repo.RefreshTokenRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import net.tokensmith.repository.repo.TokenRepository;
import net.tokensmith.authorization.register.exception.NonceException;
import net.tokensmith.authorization.security.ciphers.HashTextRandomSalt;
import net.tokensmith.pelican.Publish;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ForgotPassword {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPassword.class);
    private InsertNonce insertNonce;
    private Publish publish;
    private String issuer;
    private SpendNonce spendNonce;
    private HashTextRandomSalt hashTextRandomSalt;
    private ResourceOwnerRepository resourceOwnerRepository;
    private TokenRepository tokenRepository;
    private RefreshTokenRepository refreshTokenRepository;

    private static String EMPTY = "";

    public ForgotPassword(InsertNonce insertNonce, Publish publish, String issuer, SpendNonce spendNonce, HashTextRandomSalt hashTextRandomSalt, ResourceOwnerRepository resourceOwnerRepository, TokenRepository tokenRepository, RefreshTokenRepository refreshTokenRepository) {
        this.insertNonce = insertNonce;
        this.publish = publish;
        this.issuer = issuer;
        this.spendNonce = spendNonce;
        this.hashTextRandomSalt = hashTextRandomSalt;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.tokenRepository = tokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void sendMessage(String email) throws NonceException, BadRequestException {

        validate(email);

        String plainTextNonce;
        try {
            plainTextNonce = insertNonce.insert(email, NonceName.RESET_PASSWORD);
        } catch (NonceException e) {
            throw e;
        }

        Map<String, String> msg = new HashMap<>();
        msg.put(MessageKey.TYPE.toString(), MessageType.FORGOT_PASSWORD.toString());
        msg.put(MessageKey.RECIPIENT.toString(), email);
        msg.put(MessageKey.BASE_LINK.toString(), issuer + "/update-password?nonce=");
        msg.put(MessageKey.NONCE.toString(), plainTextNonce);

        publish.send(Topic.MAILER.toString(), msg);
    }

    protected void validate(String email) throws BadRequestException {
        if(!hasValue(email)) {
            throw new BadRequestException("email is invalid", "email", "Email is required");
        }
    }

    protected Boolean hasValue(String value) {
        if (value == null || EMPTY.equals(value.trim())) {
            return false;
        }
        return true;
    }

    public void reset(String jwt, String password, String repeatPassword) throws NotFoundException, BadRequestException {
        validate(jwt, password, repeatPassword);

        Nonce nonce;
        try {
            nonce = spendNonce.spend(jwt, NonceName.RESET_PASSWORD);
        } catch (JwtException e ) {
            throw new BadRequestException("jwt is invalid", "nonce", "Nonce was invalid", e);
        } catch (NotFoundException e) {
            throw e;
        }

        UUID resourceOwnerId = nonce.getResourceOwner().getId();
        String hashedPassword = hashTextRandomSalt.run(password);
        resourceOwnerRepository.updatePassword(resourceOwnerId, hashedPassword);

        tokenRepository.revokeActive(resourceOwnerId);
        refreshTokenRepository.revokeActive(resourceOwnerId);

        Map<String, String> msg = new HashMap<>();
        msg.put(MessageKey.TYPE.toString(), MessageType.PASSWORD_WAS_RESET.toString());
        msg.put(MessageKey.RECIPIENT.toString(), nonce.getResourceOwner().getEmail());

        publish.send(Topic.MAILER.toString(), msg);
    }

    public boolean verifyNonce(String nonce) throws NonceException {
        try {
            toJwt(nonce);
        } catch (JwtException e) {
            throw new NonceException("Nonce was not a JWT", e);
        }
        return true;
    }

    protected JsonWebToken<NonceClaim> toJwt(String from) throws JwtException {
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken<NonceClaim> to;
        try {
            to = jwtSerde.stringToJwt(from, NonceClaim.class);
        } catch (JsonToJwtException e) {
            throw new JwtException("Could not marshal to JsonWebToken<NonceClaim> from compact jwt", e);
        } catch (InvalidJWT e) {
            throw new JwtException("Input was not a JWT", e);
        }

        return to;
    }

    protected void validate(String jwt, String password, String repeatPassword) throws BadRequestException {
        if(!hasValue(jwt)) {
            throw new BadRequestException("jwt is invalid", "nonce", "Nonce is required");
        }

        if(!hasValue(password)) {
            throw new BadRequestException("password is invalid", "password", "Password is required");
        }

        if(!hasValue(repeatPassword)) {
            throw new BadRequestException("repeat password is invalid", "repeatPassword", "Repeat Password is required");
        }

        if (!password.equals(repeatPassword)) {
            throw new BadRequestException("passwords do not match", "password", "Passwords do not match");
        }
    }
}
