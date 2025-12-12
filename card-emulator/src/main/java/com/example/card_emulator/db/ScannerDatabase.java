package com.example.card_emulator.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ScanTransaction.class}, version = 1, exportSchema = false)
public abstract class ScannerDatabase extends RoomDatabase {
    public abstract ScannerDao scannerDao();

    private static volatile ScannerDatabase INSTANCE;

    public static ScannerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ScannerDatabase.class) {
                if (INSTANCE == null) {
                    // SEPARATE FILE: "scanner_pos.db"
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ScannerDatabase.class, "scanner_pos.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}