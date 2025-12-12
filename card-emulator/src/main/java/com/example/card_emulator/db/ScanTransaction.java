package com.example.card_emulator.db;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_history")
public class ScanTransaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String studentName;
    public String token;
    public double amount;
    public long timestamp;
    public String status; // "SUCCESS" or "FAILED"

    public ScanTransaction(String studentName, String token, double amount, String status) {
        this.studentName = studentName;
        this.token = token;
        this.amount = amount;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }
}