package org.rootservices.authorization.authenticate;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/25/15.
 */
@Component
public class LoginConfidentialClientImpl implements LoginConfidentialClient {

    private MatchPasswords matchPasswords;
    private ConfidentialClientRepository confidentialClientRepository;

    @Autowired
    public LoginConfidentialClientImpl(MatchPasswords matchPasswords, ConfidentialClientRepository confidentialClientRepository) {
        this.matchPasswords = matchPasswords;
        this.confidentialClientRepository = confidentialClientRepository;

    }
    @Override
    public ConfidentialClient run(UUID clientUUID, String plainTextPassword) throws UnauthorizedException {
        ConfidentialClient confidentialClient = null;
        try {
            confidentialClient = confidentialClientRepository.getByClientId(clientUUID);
        } catch (RecordNotFoundException e) {
            throw new UnauthorizedException(
                    ErrorCode.CLIENT_NOT_FOUND.getDescription(),
                    e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        boolean passwordsMatch = matchPasswords.run(
                plainTextPassword, confidentialClient.getPassword()
        );

        if (!passwordsMatch) {
            throw new UnauthorizedException(
                    ErrorCode.PASSWORD_MISMATCH.getDescription(),
                    ErrorCode.PASSWORD_MISMATCH.getCode()
            );
        }
        return confidentialClient;
    }
}