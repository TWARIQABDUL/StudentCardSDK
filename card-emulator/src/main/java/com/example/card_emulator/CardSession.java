package com.example.card_emulator;

public class CardSession {
    private static CardSession instance;
    private String studentToken;
    private boolean isEnabled = false;

    private CardSession() {}

    public static synchronized CardSession getInstance() {
        if (instance == null) {
            instance = new CardSession();
        }
        return instance;
    }

    public void setToken(String token) {
        this.studentToken = token;
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
        this.studentToken = null;
    }

    public String getToken() {
        return studentToken;
    }

    public boolean isReady() {
        return isEnabled && studentToken != null;
    }
}