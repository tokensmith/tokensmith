package net.tokensmith.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.repository.entity.RSAPrivateKeyBytes;
import net.tokensmith.repository.repo.RsaPrivateKeyRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.RSAPrivateKeyMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/16/16.
 */
public class RsaPrivateKeyRepoTest {

    @Mock
    private RSAPrivateKeyMapper mockRsaPrivateKeyMapper;
    private RsaPrivateKeyRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SymmetricKey dbKey = new SymmetricKey(
                Optional.of("2019117"), "LjF8D5qi24-dJQRFeAshXmJLhtQzn62iLt8f5ftDR_Q", Use.ENCRYPTION
        );
        subject = new RsaPrivateKeyRepo(mockRsaPrivateKeyMapper, new JwtAppFactory(), dbKey);
    }

    @Test
    public void testInsert() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        subject.insert(rsaPrivateKey);

        verify(mockRsaPrivateKeyMapper).insert(any(RSAPrivateKeyBytes.class));
    }

    @Test
    public void getMostRecentAndActiveForSigningShouldFindRecord() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        RSAPrivateKeyBytes encryptedKey = subject.encrypt(rsaPrivateKey);

        when(mockRsaPrivateKeyMapper.getMostRecentAndActiveForSigning()).thenReturn(encryptedKey);

        RSAPrivateKey actual = subject.getMostRecentAndActiveForSigning();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(rsaPrivateKey.getId()));
        assertThat(actual.getModulus(), is(rsaPrivateKey.getModulus()));
        assertThat(actual.getPublicExponent(), is(rsaPrivateKey.getPublicExponent()));
        assertThat(actual.getPrivateExponent(), is(rsaPrivateKey.getPrivateExponent()));
        assertThat(actual.getPrimeP(), is(rsaPrivateKey.getPrimeP()));
        assertThat(actual.getPrimeQ(), is(rsaPrivateKey.getPrimeQ()));
        assertThat(actual.getPrimeExponentP(), is(rsaPrivateKey.getPrimeExponentP()));
        assertThat(actual.getPrimeExponentQ(), is(rsaPrivateKey.getPrimeExponentQ()));
        assertThat(actual.getCrtCoefficient(), is(rsaPrivateKey.getCrtCoefficient()));
        assertThat(actual.getCreatedAt(), is(rsaPrivateKey.getCreatedAt()));
        assertThat(actual.getUpdatedAt(), is(rsaPrivateKey.getUpdatedAt()));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getMostRecentAndActiveForSigningShouldThrowRecordNotFound() throws Exception {
        when(mockRsaPrivateKeyMapper.getMostRecentAndActiveForSigning()).thenReturn(null);
        subject.getMostRecentAndActiveForSigning();
    }

    @Test
    public void getWhereActiveAndUseIsSignShouldReturnList() throws Exception {
        List<RSAPrivateKey> actual = subject.getWhereActiveAndUseIsSign(10, 0);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void getByIdActiveSignShouldReturnRecord() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        RSAPrivateKeyBytes encryptedKey = subject.encrypt(rsaPrivateKey);

        when(mockRsaPrivateKeyMapper.getByIdActiveSign(rsaPrivateKey.getId())).thenReturn(encryptedKey);

        RSAPrivateKey actual = subject.getByIdActiveSign(rsaPrivateKey.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(rsaPrivateKey.getId()));
        assertThat(actual.getModulus(), is(rsaPrivateKey.getModulus()));
        assertThat(actual.getPublicExponent(), is(rsaPrivateKey.getPublicExponent()));
        assertThat(actual.getPrivateExponent(), is(rsaPrivateKey.getPrivateExponent()));
        assertThat(actual.getPrimeP(), is(rsaPrivateKey.getPrimeP()));
        assertThat(actual.getPrimeQ(), is(rsaPrivateKey.getPrimeQ()));
        assertThat(actual.getPrimeExponentP(), is(rsaPrivateKey.getPrimeExponentP()));
        assertThat(actual.getPrimeExponentQ(), is(rsaPrivateKey.getPrimeExponentQ()));
        assertThat(actual.getCrtCoefficient(), is(rsaPrivateKey.getCrtCoefficient()));
        assertThat(actual.getCreatedAt(), is(rsaPrivateKey.getCreatedAt()));
        assertThat(actual.getUpdatedAt(), is(rsaPrivateKey.getUpdatedAt()));

    }

    @Test(expected = RecordNotFoundException.class)
    public void getByIdActiveSignShouldReturnKey() throws Exception {
        UUID id = UUID.randomUUID();
        when(mockRsaPrivateKeyMapper.getByIdActiveSign(id)).thenReturn(null);

        subject.getByIdActiveSign(id);
    }
}