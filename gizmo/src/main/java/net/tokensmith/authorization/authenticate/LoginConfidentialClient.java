package net.tokensmith.authorization.authenticate;

import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/25/15.
 */
@Component
public class LoginConfidentialClient {

    private MatchPasswords matchPasswords;
    private ConfidentialClientRepository confidentialClientRepository;

    @Autowired
    public LoginConfidentialClient(MatchPasswords matchPasswords, ConfidentialClientRepository confidentialClientRepository) {
        this.matchPasswords = matchPasswords;
        this.confidentialClientRepository = confidentialClientRepository;

    }

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