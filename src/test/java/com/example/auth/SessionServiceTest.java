package com.example.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class SessionServiceTest {

    private static final Instant T0 = Instant.parse("2026-09-01T00:00:00Z");

    @Test
    void issuesAValidSession() {
        SessionService sessions = new SessionService(() -> T0);
        SessionService.Session session = sessions.issue("acct-1001");
        assertTrue(sessions.validate(session.token()));
        assertEquals(1, sessions.activeCount());
    }

    @Test
    void refusesABlankSubject() {
        SessionService sessions = new SessionService(() -> T0);
        assertThrows(IllegalArgumentException.class, () -> sessions.issue(" "));
    }

    @Test
    void stopsValidatingAfterTheLifetime() {
        Instant[] now = {T0};
        SessionService sessions = new SessionService(() -> now[0]);
        SessionService.Session session = sessions.issue("acct-1001");
        now[0] = T0.plus(SessionService.LIFETIME).plusSeconds(1);
        assertFalse(sessions.validate(session.token()));
        assertEquals(0, sessions.activeCount());
    }

    @Test
    void revokeRemovesTheSessionOnce() {
        SessionService sessions = new SessionService(() -> T0);
        SessionService.Session session = sessions.issue("acct-1001");
        assertTrue(sessions.revoke(session.token()));
        assertFalse(sessions.revoke(session.token()));
        assertFalse(sessions.validate(session.token()));
    }

    @Test
    void unknownTokensAreNotValid() {
        SessionService sessions = new SessionService(() -> T0);
        assertFalse(sessions.validate("nope"));
        assertFalse(sessions.validate(null));
    }
}
