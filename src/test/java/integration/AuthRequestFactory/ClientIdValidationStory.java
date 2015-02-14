package integration.AuthRequestFactory;

import integration.AuthRequestFactory.steps.ClientIdValidationSteps;
import integration.BaseStory;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by tommackenzie on 2/8/15.
 */
public class ClientIdValidationStory extends BaseStory<ClientIdValidationSteps> {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Override
    public ClientIdValidationSteps makeSteps() {
        return new ClientIdValidationSteps(authRequestFactory);
    }
}
