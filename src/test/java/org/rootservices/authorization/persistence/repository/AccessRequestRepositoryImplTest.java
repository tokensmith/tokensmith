package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.AccessRequestMapper;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/15/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessRequestRepositoryImplTest {

    @Mock
    private AccessRequestMapper mockAuthRequestMapper;

    private AccessRequestRepository subject;

    @Before
    public void setUp() {
        subject = new AccessRequestRepositoryImpl(mockAuthRequestMapper);
    }

    @Test
    public void insert() throws Exception {
        AccessRequest accessRequest = new AccessRequest();
        subject.insert(accessRequest);
        verify(mockAuthRequestMapper).insert(accessRequest);
    }

    @Test
    public void getByClientUUIDAndAuthCode() throws RecordNotFoundException {
        AccessRequest expected = new AccessRequest();

        UUID clientUUID = UUID.randomUUID();
        String code = "valid-authorization-code";

        when(mockAuthRequestMapper.getByClientUUIDAndAuthCode(clientUUID, code)).thenReturn(expected);
        AccessRequest actual = subject.getByClientUUIDAndAuthCode(clientUUID, code);

        assertThat(actual).isEqualTo(expected);

    }

    @Test
    public void getByClientUUIDAndAuthCodeRecordNotFound(){

        UUID clientUUID = UUID.randomUUID();
        String code = "invalid-authorization-code";

        when(mockAuthRequestMapper.getByClientUUIDAndAuthCode(clientUUID, code)).thenReturn(null);
        AccessRequest actual = null;

        try{
            actual = subject.getByClientUUIDAndAuthCode(clientUUID, code);
            fail("Expected RecordNotFoundException");
        } catch (RecordNotFoundException e) {
            assertThat(actual).isNull();
        }
    }
}