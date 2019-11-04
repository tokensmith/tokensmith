package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


/**
 * Created by tommackenzie on 5/12/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class ScopeMapperTest {

    @Autowired
    private ScopeMapper subject;

    @Test
    public void insert() {
        Scope scope = new Scope(UUID.randomUUID(), "some-scope");
        subject.insert(scope);
    }

    @Test
    public void findByNames() {
        String scopeName = "some-scope";
        Scope scope = new Scope(UUID.randomUUID(), scopeName);
        subject.insert(scope);

        List<String> names = new ArrayList<>();
        names.add(scopeName);
        List<Scope> scopes = subject.findByNames(names);

        assertThat(scopes.size(), is(1));
        assertThat(scopes.get(0).getId(), is(notNullValue()));
        assertThat(scopes.get(0).getName(), is(scopeName));
        assertThat(scopes.get(0).getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void findByName() {
        String scopeName = "some-scope";
        Scope scope = new Scope(UUID.randomUUID(), scopeName);
        subject.insert(scope);

        Scope actual = subject.findByName(scopeName);

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getName(), is(scopeName));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }
}
