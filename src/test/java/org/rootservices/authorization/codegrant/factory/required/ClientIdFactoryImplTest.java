package org.rootservices.authorization.codegrant.factory.required;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.codegrant.factory.exception.DataTypeException;
import org.rootservices.authorization.codegrant.factory.required.ClientIdFactoryImpl;
import org.rootservices.authorization.codegrant.validator.RequiredParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientIdFactoryImplTest {

    @Mock
    private RequiredParam mockRequiredParam;

    private ClientIdFactory subject;

    @Before
    public void setUp() {
        subject = new ClientIdFactoryImpl(mockRequiredParam);
    }

    @Test
    public void testMakeClientId() throws Exception {
        UUID expected = UUID.randomUUID();
        List<String> items = new ArrayList<>();
        items.add(expected.toString());

        when(mockRequiredParam.run(items)).thenReturn(true);

        UUID actual = subject.makeClientId(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected= DataTypeException.class)
    public void testMakeClientIdNotAUuid() throws Exception {

        List<String> items = new ArrayList<>();
        items.add("not-a-uuid");

        when(mockRequiredParam.run(items)).thenReturn(true);

        subject.makeClientId(items);
    }
}