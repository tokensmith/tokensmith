package integration.AuthRequestFactory;

import integration.AuthRequestFactory.steps.responseType.InformResourceOwnerSteps;
import integration.BaseStory;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tommackenzie on 2/11/15.
 */
public class ResponseTypeValidationInformResourceOwnerStory extends BaseStory<InformResourceOwnerSteps> {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Override
    public InformResourceOwnerSteps makeSteps() {
        return new InformResourceOwnerSteps(authRequestFactory);
    }
}
