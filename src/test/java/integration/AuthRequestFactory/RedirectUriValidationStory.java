package integration.AuthRequestFactory;

import integration.AuthRequestFactory.steps.RedirectUriValidationSteps;
import integration.BaseStory;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tommackenzie on 2/13/15.
 */
public class RedirectUriValidationStory extends BaseStory<RedirectUriValidationSteps> {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Override
    public RedirectUriValidationSteps makeSteps() {
        return new RedirectUriValidationSteps(authRequestFactory);
    }
}
