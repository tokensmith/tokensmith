package integration.AuthRequestFactory.ResponseTypeValidation.InformResourceOwner;

import integration.AuthRequestFactory.steps.ResponseTypeValidation.InformResourceOwner.ClientIsNotFoundSteps;
import integration.BaseStory;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tommackenzie on 2/11/15.
 */
public class ClientIsNotFoundStory extends BaseStory<ClientIsNotFoundSteps> {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Override
    public ClientIsNotFoundSteps makeSteps() {
        return new ClientIsNotFoundSteps(authRequestFactory);
    }
}
