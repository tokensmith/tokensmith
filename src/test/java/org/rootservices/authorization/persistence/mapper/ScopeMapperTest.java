package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/12/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
public class ScopeMapperTest {

    @Autowired
    private ScopeMapper subject;

    @Test
    @Transactional
    public void insert() {
        Scope scope = new Scope(UUID.randomUUID(), "some-scope");
        subject.insert(scope);
    }
}
