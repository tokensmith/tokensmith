package integration.AuthRequestFactory.ScopesValidation.InformResourceOwner;

import integration.AuthRequestFactory.steps.ScopesValidation.InformResourceOwner.RedirectMismatchSteps;
import integration.BaseStory;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tommackenzie on 2/21/15.
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
