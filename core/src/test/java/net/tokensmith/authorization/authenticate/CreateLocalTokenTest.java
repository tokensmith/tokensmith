package net.tokensmith.authorization.authenticate;

import net.tokensmith.authorization.authenticate.exception.LocalSessionException;
import net.tokensmith.authorization.authenticate.model.Session;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.repository.entity.LocalToken;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.repo.LocalTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateLocalTokenTest {
    public static Long ONE_DAY = 86400L;
    private CreateLocalToken subject;

    @Mock
    private RandomString mockRandomString;
    @Mock
    private HashToken mockHashToken;
    @Mock
    private LocalTokenRepository mockLocalTokenRepository;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new CreateLocalToken(mockRandomString, mockHashToken, mockLocalTokenRepository, ONE_DAY);
    }

    @Test
    public void makeSessionShouldBeOk() throws Exception {
        UUID resourceOwnerId = UUID.randomUUID();
        when(mockRandomString.run()).thenReturn("local-token");
        when(mockHashToken.run(eq("local-token"))).thenReturn("hashed-token");

        Session actual = subject.makeSession(resourceOwnerId, 1);

        assertThat(actual.getToken(), is("local-token"));
        assertThat(actual.getIssuedAt(), is(notNullValue()));

        ArgumentCaptor<LocalToken> localTokenCaptor = ArgumentCaptor.forClass(LocalToken.class);
        verify(mockLocalTokenRepository).insert(localTokenCaptor.capture());

        assertThat(localTokenCaptor.getValue().getId(), is(notNullValue()));
        assertThat(localTokenCaptor.getValue().getResourceOwnerId(), is(resourceOwnerId));
        assertThat(localTokenCaptor.getValue().getToken(), is("hashed-token"));
        assertThat(localTokenCaptor.getValue().isRevoked(), is(false));
        Long actualExpiresAt = localTokenCaptor.getValue().getExpiresAt().toEpochSecond();

        Boolean expiresCheck = (OffsetDateTime.now().toEpochSecond() - actualExpiresAt) < ONE_DAY;

        assertTrue("Checking local token expires at is roughly set ok.", expiresCheck);
    }

    @Test
    public void makeSessionShouldThrowLocalSessionException() throws Exception {
        UUID resourceOwnerId = UUID.randomUUID();
        when(mockRandomString.run()).thenReturn("local-token");
        when(mockHashToken.run(eq("local-token"))).thenReturn("hashed-token");

        DuplicateRecordException dre = new DuplicateRecordException();
        doThrow(dre).when(mockLocalTokenRepository).insert(any(LocalToken.class));

        LocalSessionException actual = null;
        try {
            subject.makeSession(resourceOwnerId, 1);
        } catch (LocalSessionException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void makeAndRevokeSessionShouldBeOk() throws Exception {
        UUID resourceOwnerId = UUID.randomUUID();
        when(mockRandomString.run()).thenReturn("local-token");
        when(mockHashToken.run(eq("local-token"))).thenReturn("hashed-token");

        Session actual = subject.makeAndRevokeSession(resourceOwnerId, 1);

        assertThat(actual.getToken(), is("local-token"));
        assertThat(actual.getIssuedAt(), is(notNullValue()));

        verify(mockLocalTokenRepository).revokeActive(eq(resourceOwnerId));

        ArgumentCaptor<LocalToken> localTokenCaptor = ArgumentCaptor.forClass(LocalToken.class);
        verify(mockLocalTokenRepository).insert(localTokenCaptor.capture());

        assertThat(localTokenCaptor.getValue().getId(), is(notNullValue()));
        assertThat(localTokenCaptor.getValue().getResourceOwnerId(), is(resourceOwnerId));
        assertThat(localTokenCaptor.getValue().getToken(), is("hashed-token"));
        assertThat(localTokenCaptor.getValue().isRevoked(), is(false));
        Long actualExpiresAt = localTokenCaptor.getValue().getExpiresAt().toEpochSecond();

        Boolean expiresCheck = (OffsetDateTime.now().toEpochSecond() - actualExpiresAt) < ONE_DAY;

        assertTrue("Checking local token expires at is roughly set ok.", expiresCheck);
    }
}