package com.example.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Issues, validates and revokes session tokens. */
public class SessionService {

    static final Duration LIFETIME = Duration.ofMinutes(30);

    private static final TokenTemplate AUDIT =
            new TokenTemplate("session ${action} subject=${subject} token=${token}");

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Clock clock;

    /** Kept as an interface so the expiry tests do not have to sleep. */
    public interface Clock {
        Instant now();
    }

    public record Session(String token, String subject, Instant issuedAt, Instant expiresAt) {}

    public SessionService() {
        this(Instant::now);
    }

    public SessionService(Clock clock) {
        this.clock = clock;
    }

    public Session issue(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("subject is required");
        }
        Instant now = clock.now();
        Session session =
                new Session(UUID.randomUUID().toString(), subject, now, now.plus(LIFETIME));
        sessions.put(session.token(), session);
        System.out.println(audit("issued", session));
        return session;
    }

    public boolean validate(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Session session = sessions.get(token);
        if (session == null) {
            return false;
        }
        if (clock.now().isAfter(session.expiresAt())) {
            sessions.remove(token);
            System.out.println(audit("expired", session));
            return false;
        }
        return true;
    }

    public boolean revoke(String token) {
        Session removed = token == null ? null : sessions.remove(token);
        if (removed == null) {
            return false;
        }
        System.out.println(audit("revoked", removed));
        return true;
    }

    public int activeCount() {
        return sessions.size();
    }

    private String audit(String action, Session session) {
        Map<String, String> values = new HashMap<>();
        values.put("action", action);
        values.put("subject", session.subject());
        // Only the first characters of the token reach the audit trail.
        values.put("token", session.token().substring(0, 8));
        return AUDIT.render(values);
    }
}
