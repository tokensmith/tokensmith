package org.rootservices.authorization.codegrant.translator;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.rootservices.authorization.codegrant.validator.HasOneItem;
import org.rootservices.authorization.codegrant.validator.IsNotNull;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringsToResponseTypeImplTest {

    @Mock
    private IsNotNull mockIsNotNull;

    @Mock
    private HasOneItem mockHasOneItem;

    private StringsToResponseType subject;

    @Before
    public void run() {
        subject = new StringsToResponseTypeImpl(mockIsNotNull, mockHasOneItem);
    }

    @Test(expected=ValidationError.class)
    public void runIsNull() throws ValidationError {
        List<String> items = null;

        when(mockIsNotNull.run(items)).thenReturn(false);

        subject.run(items);
    }

    @Test(expected=ValidationError.class)
    public void runHasTooManyItems() throws ValidationError {
        List<String> items = new ArrayList<>();
        String item = "items";
        items.add(item);
        items.add(item);

        when(mockIsNotNull.run(items)).thenReturn(true);
        when(mockHasOneItem.run(items)).thenReturn(false);

        subject.run(items);
    }

    @Test(expected=ValidationError.class)
    public void runIsNotResponseType() throws ValidationError {
        List<String> items = new ArrayList<>();
        String item = "items";
        items.add(item);

        when(mockIsNotNull.run(items)).thenReturn(true);
        when(mockHasOneItem.run(items)).thenReturn(true);

        subject.run(items);
    }

    @Test
    public void runIsResponseType() throws ValidationError {
        List<String> items = new ArrayList<>();
        ResponseType expected = ResponseType.CODE;
        String item = expected.toString();
        items.add(item);

        when(mockIsNotNull.run(items)).thenReturn(true);
        when(mockHasOneItem.run(items)).thenReturn(true);

        ResponseType actual = subject.run(items);
        assertThat(actual).isEqualTo(expected);
    }

}