package net.tokensmith.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.persistence.entity.RSAPrivateKey;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.RSAPrivateKeyMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/16/16.
 */
public class RsaPrivateKeyRepositoryImplTest {

    @Mock
    private RSAPrivateKeyMapper mockRsaPrivateKeyMapper;
    private RsaPrivateKeyRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RsaPrivateKeyRepositoryImpl(mockRsaPrivateKeyMapper);
    }

    @Test
    public void testInsert() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        subject.insert(rsaPrivateKey);

        verify(mockRsaPrivateKeyMapper).insert(rsaPrivateKey);
    }

    @Test
    public void getMostRecentAndActiveForSigningShouldFindRecord() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        when(mockRsaPrivateKeyMapper.getMostRecentAndActiveForSigning()).thenReturn(rsaPrivateKey);

        RSAPrivateKey actual = subject.getMostRecentAndActiveForSigning();

        assertThat(actual, is(rsaPrivateKey));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getMostRecentAndActiveForSigningShouldThrowRecordNotFound() throws Exception {
        when(mockRsaPrivateKeyMapper.getMostRecentAndActiveForSigning()).thenReturn(null);
        subject.getMostRecentAndActiveForSigning();
    }

    @Test
    public void getWhereActiveAndUseIsSignShouldReturnList() throws Exception {
        List<RSAPrivateKey> keys = new ArrayList<>();
        when(mockRsaPrivateKeyMapper.getWhereActiveAndUseIsSign(10, 0)).thenReturn(keys);

        List<RSAPrivateKey> actual = subject.getWhereActiveAndUseIsSign(10, 0);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void getByIdActiveSignShouldReturnRecord() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        when(mockRsaPrivateKeyMapper.getByIdActiveSign(rsaPrivateKey.getId())).thenReturn(rsaPrivateKey);

        RSAPrivateKey actual = subject.getByIdActiveSign(rsaPrivateKey.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(rsaPrivateKey));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByIdActiveSignShouldReturnKey() throws Exception {
        UUID id = UUID.randomUUID();
        when(mockRsaPrivateKeyMapper.getByIdActiveSign(id)).thenReturn(null);

        subject.getByIdActiveSign(id);
    }
}