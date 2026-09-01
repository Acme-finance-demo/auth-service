package com.example.auth;

public final class AuthApp {

    public static void main(String[] args) {
        SessionService sessions = new SessionService();
        SessionService.Session session = sessions.issue("acct-1001");
        System.out.println("valid=" + sessions.validate(session.token()));
        sessions.revoke(session.token());
        System.out.println("valid after revoke=" + sessions.validate(session.token()));
    }
}
