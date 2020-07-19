package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.AccessRequestMapper;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.repository.entity.AccessRequest;
import net.tokensmith.repository.repo.AccessRequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 4/15/15.
 */
public class AccessRequestRepoTest {

    @Mock
    private AccessRequestMapper mockAuthRequestMapper;

    private AccessRequestRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SymmetricKey dbKey = new SymmetricKey(
                Optional.of("2019117"), "LjF8D5qi24-dJQRFeAshXmJLhtQzn62iLt8f5ftDR_Q", Use.ENCRYPTION
        );
        subject = new AccessRequestRepo(mockAuthRequestMapper, new JwtAppFactory(), dbKey);
    }

    @Test
    public void insert() throws Exception {
        AccessRequest accessRequest = new AccessRequest();
        subject.insert(accessRequest);
        verify(mockAuthRequestMapper).insert(accessRequest);
    }
}