package com.example.card_emulator.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wallet_cache")
public class WalletEntity {

    @PrimaryKey
    @NonNull
    public String studentToken; // e.g. "STU-2025-..."

    public String name;
    public String email;
    public String role;         // "STUDENT", "GUARD"
    public double balance;
    public String validUntil;   // ISO Date String e.g. "2029-12-16T13:46:15"
    public boolean isActive;

    public long lastUpdated;

    // Default Constructor for Room
    public WalletEntity() {}

    // Convenience Constructor
    public WalletEntity(@NonNull String studentToken, String name, String email,
                        String role, double balance, String validUntil, boolean isActive) {
        this.studentToken = studentToken;
        this.name = name;
        this.email = email;
        this.role = role;
        this.balance = balance;
        this.validUntil = validUntil;
        this.isActive = isActive;
        this.lastUpdated = System.currentTimeMillis();
    }
}