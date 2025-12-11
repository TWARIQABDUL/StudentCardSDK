package com.example.card_emulator.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wallet_cache")
public class WalletEntity {

    @PrimaryKey
    @NonNull
    public String studentToken; // e.g. "STUDENT-ID-12345-SECURE"

    public double balance;      // e.g. 95.00

    public long lastUpdated;    // Timestamp in milliseconds

    // Constructor
    public WalletEntity(@NonNull String studentToken, double balance) {
        this.studentToken = studentToken;
        this.balance = balance;
        this.lastUpdated = System.currentTimeMillis();
    }
}