package net.tokensmith.authorization.openId.identity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 9/5/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public class MakeAccessTokenHashTest {
    @Autowired
    private MakeAccessTokenHash subject;

    @Test
    public void makeEncodedHashShouldBeOk() throws Exception {
        String input = "some-access-token";

        String actual = subject.makeEncodedHash(input);
        assertThat(actual, is("CRLvO23C6lecaPrHhPjC3Q=="));
    }
}