package org.rootservices.authorization.nonce.reset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.authorization.nonce.SpendNonce;
import org.rootservices.authorization.nonce.InsertNonce;
import org.rootservices.authorization.nonce.message.MessageKey;
import org.rootservices.authorization.nonce.message.MessageType;
import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.nonce.message.Topic;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.repository.RefreshTokenRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.register.exception.NonceException;
import org.rootservices.authorization.security.ciphers.HashTextRandomSalt;
import org.rootservices.pelican.Publish;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResetPassword {
    private static final Logger logger = LogManager.getLogger(ResetPassword.class);
    private InsertNonce insertNonce;
    private Publish publish;
    private String issuer;
    private SpendNonce spendNonce;
    private HashTextRandomSalt hashTextRandomSalt;
    private ResourceOwnerRepository resourceOwnerRepository;
    private TokenRepository tokenRepository;
    private RefreshTokenRepository refreshTokenRepository;


    public void sendMessage(String email) throws NonceException {

        String plainTextNonce = null;
        try {
            plainTextNonce = insertNonce.insert(email, NonceName.RESET_PASSWORD);
        } catch (NonceException e) {
            throw e;
        }

        Map<String, String> msg = new HashMap<>();
        msg.put(MessageKey.TYPE.toString(), MessageType.RESET_PASSWORD.toString());
        msg.put(MessageKey.RECIPIENT.toString(), email);
        msg.put(MessageKey.RECIPIENT.toString(), issuer + "/reset?nonce=");
        msg.put(MessageKey.NONCE.toString(), plainTextNonce);

        publish.send(Topic.MAILER.toString(), msg);

    }

    public void reset(String jwt, String password) throws NotFoundException, BadRequestException {
        Nonce nonce;
        try {
            nonce = spendNonce.spend(jwt, NonceName.RESET_PASSWORD);
        } catch (BadRequestException e) {
            throw e;
        } catch (NotFoundException e) {
            throw e;
        }

        UUID resourceOwnerId = nonce.getResourceOwner().getId();
        String hashedPassword = hashTextRandomSalt.run(password);
        resourceOwnerRepository.updatePassword(resourceOwnerId, hashedPassword.getBytes());

        tokenRepository.revokeActive(resourceOwnerId);
        refreshTokenRepository.revokeActive(resourceOwnerId);

        // send user a email saying their password was reset.

    }
}
