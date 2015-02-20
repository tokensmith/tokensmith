package integration.AuthRequestFactory.steps.ScopesValidation;

import integration.AuthRequestFactory.steps.ExceptionSteps;
import org.jbehave.core.annotations.Given;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommackenzie on 2/20/15.
 */
public class CommonSteps extends ExceptionSteps {

    protected List<String> scopes;

    @Given("the parameter scopes has two items assigned to PROFILE")
    public void theParameterScopesHasTwoItemsAssignedToProfile() {
        this.scopes = new ArrayList<>();
        this.scopes.add("PROFILE");
        this.scopes.add("PROFILE");
    }

    @Given("the parameter scopes has one item assigned to UNKNOWN_SCOPE")
    public void theParameterScopesHasOneItemAssignedToUnkownScope() {
        this.scopes = new ArrayList<>();
        this.scopes.add("UnknownScope");
    }

    @Given("the parameter scopes has one item assigned to a empty string")
    public void theParameterScopesHasOneItemAssignedToAEmptyString() {
        this.scopes = new ArrayList<>();
        this.scopes.add("");
    }
}
