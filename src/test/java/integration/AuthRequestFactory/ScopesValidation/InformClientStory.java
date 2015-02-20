package integration.AuthRequestFactory.ScopesValidation;

import integration.AuthRequestFactory.steps.ScopesValidation.InformClientSteps;
import integration.BaseStory;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tommackenzie on 2/13/15.
 */
public class InformClientStory extends BaseStory<InformClientSteps> {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public InformClientSteps makeSteps() {
        return new InformClientSteps(authRequestFactory, clientRepository);
    }

}
