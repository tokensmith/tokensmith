package org.rootservices.authorization.parse;


import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.parse.exception.OptionalException;
import org.rootservices.authorization.parse.exception.RequiredException;
import org.rootservices.authorization.parse.validator.OptionalParam;
import org.rootservices.authorization.parse.validator.RequiredParam;

import java.net.URI;
import java.util.*;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class ParserTest {
    private Parser subject;

    // names of fields expected
    private List<String> names = Arrays.asList(
            "string", "id", "uri",
            "strings", "ids", "uris",
            "optString", "optId", "optUri"
    );

    @Before
    public void setUp() {
        subject = new Parser(
            new OptionalParam(), new RequiredParam()
        );
    }

    public Map<String, List<String>> makeParameters() {
        Map<String, List<String>> parameters = new HashMap<>();

        List<String> strings = Arrays.asList("string1");
        parameters.put("string", strings);

        List<String> strings2 = Arrays.asList("string1 string2 string3");
        parameters.put("strings", strings2);

        parameters.put("opt_string", strings);

        List<String> uuids = Arrays.asList(UUID.randomUUID().toString());
        parameters.put("uuid", uuids);
        parameters.put("uuids", uuids);
        parameters.put("opt_uuid", uuids);

        List<String> uris = Arrays.asList("https://rootservices.org");
        parameters.put("uri", uris);
        parameters.put("uris", uris);
        parameters.put("opt_uri", uris);

        return parameters;
    }

    @Test
    public void reflectShouldFindAllFields() throws Exception {
        List<ParamEntity> actuals = subject.reflect(Dummy.class);
        assertThat(actuals, is(notNullValue()));
        assertThat(actuals.size(), is(names.size()));

        for(ParamEntity actual: actuals) {
            boolean found = names.contains(actual.getField().getName());
            assertTrue("could not find field: " + actual.getField().getName(), found);
        }
    }

    @Test
    public void toShouldTranslate() throws Exception {
        List<ParamEntity> fields = subject.reflect(Dummy.class);
        Map<String, List<String>> params = makeParameters();

        Dummy actual = (Dummy) subject.to(Dummy.class, fields, params);

        assertThat(actual, is(notNullValue()));

        // string
        String expectedString = params.get("string").get(0);
        assertThat(actual.getString(), is(notNullValue()));
        assertThat(actual.getString(), is(expectedString));

        assertThat(actual.getStrings(), is(notNullValue()));
        assertThat(actual.getStrings().size(), is(3));
        assertThat(actual.getStrings().get(0), is("string1"));
        assertThat(actual.getStrings().get(1), is("string2"));
        assertThat(actual.getStrings().get(2), is("string3"));

        assertThat(actual.getOptString(), is(notNullValue()));
        assertThat(actual.getOptString().isPresent(), is(true));
        assertThat(actual.getOptString().get(), is(expectedString));

        // id
        UUID expectedId = UUID.fromString(params.get("uuid").get(0));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getId(), is(expectedId));

        assertThat(actual.getIds(), is(notNullValue()));
        assertThat(actual.getIds().size(), is(1));
        assertThat(actual.getIds().get(0), is(expectedId));

        assertThat(actual.getOptId(), is(notNullValue()));
        assertThat(actual.getOptId().isPresent(), is(true));
        assertThat(actual.getOptId().get(), is(expectedId));

        // uri
        URI expectedUri = new URI(params.get("uri").get(0));
        assertThat(actual.getUri(), is(notNullValue()));
        assertThat(actual.getUri(), is(expectedUri));

        assertThat(actual.getUris(), is(notNullValue()));
        assertThat(actual.getUris().size(), is(1));
        assertThat(actual.getUris().get(0), is(expectedUri));

        assertThat(actual.getOptUri(), is(notNullValue()));
        assertThat(actual.getOptUri().isPresent(), is(true));
        assertThat(actual.getOptUri().get(), is(expectedUri));

        // not annotated field - should not have been assigned.
        assertThat(actual.getNotAnnotated(), is(nullValue()));
    }

    @Test
    public void toWhenOptFieldIsMissingShouldMakeAEmptyValue() throws Exception {
        List<ParamEntity> fields = subject.reflect(Dummy.class);

        // modify uuids to have a empty value at index 0
        Map<String, List<String>> params = makeParameters();
        params.remove("opt_uuid");

        Dummy actual = (Dummy) subject.to(Dummy.class, fields, params);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getOptId(), is(notNullValue()));
        assertThat(actual.getOptId().isPresent(), is(false));
    }

    @Test(expected = RequiredException.class)
    public void toWhenMissingReqFieldShouldThrowRequiredException() throws Exception {
        List<ParamEntity> fields = subject.reflect(Dummy.class);

        // remove the key, string
        Map<String, List<String>> params = makeParameters();
        params.remove("string");

        subject.to(Dummy.class, fields, params);
    }

    @Test
    public void toWhenOptFieldIsEmptyShouldThrowOptionalException() throws Exception {
        List<ParamEntity> fields = subject.reflect(Dummy.class);

        // modify uuids to have a empty value at index 0
        Map<String, List<String>> params = makeParameters();
        List<String> uuid = Arrays.asList("");
        params.put("opt_uuid", uuid);

        OptionalException actualException = null;
        try {
            subject.to(Dummy.class, fields, params);
        } catch (OptionalException e) {
            actualException = e;
        }

        assertThat(actualException, is(notNullValue()));
        Dummy actual = (Dummy) actualException.getTarget();

        assertThat(actual, is(notNullValue()));

        // should have populated all the vars above the failure point

        String expectedString = params.get("string").get(0);
        assertThat(actual.getString(), is(notNullValue()));
        assertThat(actual.getString(), is(expectedString));

        UUID expectedId = UUID.fromString(params.get("uuid").get(0));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getId(), is(expectedId));

        URI expectedUri = new URI(params.get("uri").get(0));
        assertThat(actual.getUri(), is(notNullValue()));
        assertThat(actual.getUri(), is(expectedUri));

        // lists
        assertThat(actual.getStrings(), is(notNullValue()));
        assertThat(actual.getStrings().size(), is(3));
        assertThat(actual.getStrings().get(0), is("string1"));
        assertThat(actual.getStrings().get(1), is("string2"));
        assertThat(actual.getStrings().get(2), is("string3"));

        assertThat(actual.getIds(), is(notNullValue()));
        assertThat(actual.getIds().size(), is(1));
        assertThat(actual.getIds().get(0), is(expectedId));

        assertThat(actual.getUris(), is(notNullValue()));
        assertThat(actual.getUris().size(), is(1));
        assertThat(actual.getUris().get(0), is(expectedUri));

        // opts
        assertThat(actual.getOptString(), is(notNullValue()));
        assertThat(actual.getOptString().isPresent(), is(true));
        assertThat(actual.getOptString().get(), is(expectedString));

        // these should not have been assigned.
        assertThat(actual.getOptId(), is(nullValue()));
        assertThat(actual.getOptUri(), is(nullValue()));

        // not annotated field - should not have been assigned.
        assertThat(actual.getNotAnnotated(), is(nullValue()));
    }
}