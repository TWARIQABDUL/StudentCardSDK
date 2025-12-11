package com.example.card_emulator;

public interface BalanceCallback {
    void onBalanceLoaded(double balance);
    void onError(String message);
}