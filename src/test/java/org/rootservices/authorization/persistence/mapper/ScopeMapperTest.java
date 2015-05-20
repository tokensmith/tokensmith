package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

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

    @Test
    @Transactional
    public void findByName() {
        String scopeName = "some-scope";
        Scope scope = new Scope(UUID.randomUUID(), scopeName);
        subject.insert(scope);

        List<String> names = new ArrayList<>();
        names.add(scopeName);
        List<Scope> scopes = subject.findByName(names);

        assertThat(scopes.size()).isEqualTo(1);
        assertThat(scopes.get(0).getUuid()).isNotNull();
        assertThat(scopes.get(0).getName()).isEqualTo(scopeName);
        assertThat(scopes.get(0).getCreatedAt()).isNotNull();
    }
}
