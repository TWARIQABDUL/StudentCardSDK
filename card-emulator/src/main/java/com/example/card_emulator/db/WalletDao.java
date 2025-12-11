package com.example.card_emulator.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface WalletDao {

    // SAVE: Updates the balance when we get fresh data from the server
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWallet(WalletEntity wallet);

    // READ: Gets the last known balance for the UI
    @Query("SELECT * FROM wallet_cache WHERE studentToken = :token LIMIT 1")
    WalletEntity getWallet(String token);

    // CLEAR: Call this when the user logs out
    @Query("DELETE FROM wallet_cache")
    void clearAll();
}