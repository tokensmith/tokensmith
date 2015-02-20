package integration.AuthRequestFactory.steps.ResponseTypeValidation;

import integration.AuthRequestFactory.steps.ExceptionSteps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.rootservices.authorization.codegrant.exception.BaseInformException;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.factory.exception.ResponseTypeException;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 2/11/15.
 */
public abstract class CommonSteps extends ExceptionSteps {

    protected List<String> responseTypes;

    @Given("the parameter response types is assigned null")
    public void setResponseTypesNull() {
        this.responseTypes = null;
    }

    @Given("the parameter response types has no items")
    public void setResponseTypesEmptyList() {
        this.responseTypes = new ArrayList<>();
    }

    @Given("the parameter response types has one item and it's not CODE")
    public void setResponseTypesNotCode() {
        this.responseTypes = new ArrayList<>();
        this.responseTypes.add("unknown");
    }

    @Given("the parameter response types has two items assigned to CODE")
    public void setResponseTypesTwoItems() {
        this.responseTypes = new ArrayList<>();
        responseTypes.add("CODE");
        responseTypes.add("CODE");
    }
}
