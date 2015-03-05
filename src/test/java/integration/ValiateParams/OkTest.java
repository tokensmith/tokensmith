package integration.ValiateParams;

import helper.FixtureFactory;
import helper.ValidateParamsAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.grant.ValidateParams;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class OkTest extends BaseTest {

    @Test
    public void requiredParams() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = FixtureFactory.makeClient();
        clientRepository.insert(c);

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());

        boolean actual;
        actual = subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
        assertThat(actual).isEqualTo(true);
    }

    @Test
    public void optionalParams() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = FixtureFactory.makeClient();
        clientRepository.insert(c);

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.scopes.add(Scope.PROFILE.toString());
        p.states.add("some-state");

        boolean actual;
        actual = subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
        assertThat(actual).isEqualTo(true);
    }
}
