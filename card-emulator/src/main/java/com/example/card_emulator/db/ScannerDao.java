package com.example.card_emulator.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ScannerDao {
    @Insert
    void insert(ScanTransaction transaction);

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    List<ScanTransaction> getAll();

    @Query("SELECT SUM(amount) FROM scan_history WHERE status = 'SUCCESS'")
    double getTotalSales();

    @Query("DELETE FROM scan_history")
    void clearHistory();
}