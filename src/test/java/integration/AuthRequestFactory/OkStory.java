package integration.AuthRequestFactory;

import integration.AuthRequestFactory.steps.OkSteps;
import integration.BaseStory;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by tommackenzie on 2/4/15.
 */
public class OkStory extends BaseStory<OkSteps> {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Override
    public OkSteps makeSteps() {
        return new OkSteps(authRequestFactory);
    }
}
