package integration.AuthRequestFactory.ResponseTypeValidation.InformResourceOwner;

import integration.AuthRequestFactory.steps.ResponseTypeValidation.InformResourceOwner.RedirectMismatchSteps;
import integration.BaseStory;
import org.jbehave.core.annotations.Given;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
public class RedirectUriMismatchStory extends BaseStory<RedirectMismatchSteps> {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public RedirectMismatchSteps makeSteps() {
        return new RedirectMismatchSteps(authRequestFactory, clientRepository);
    }


}
