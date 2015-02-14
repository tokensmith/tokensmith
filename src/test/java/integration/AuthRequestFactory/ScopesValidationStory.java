package integration.AuthRequestFactory;

import integration.AuthRequestFactory.steps.ScopesValidationSteps;
import integration.BaseStory;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tommackenzie on 2/13/15.
 */
public class ScopesValidationStory extends BaseStory<ScopesValidationSteps> {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public ScopesValidationSteps makeSteps() {
        return new ScopesValidationSteps(authRequestFactory, clientRepository);
    }

}
