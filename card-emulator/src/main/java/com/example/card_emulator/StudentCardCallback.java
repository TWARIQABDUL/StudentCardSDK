package com.example.card_emulator;
public interface StudentCardCallback {
    void onScanSuccess(String studentId);
    void onScanError(String errorMessage);
}