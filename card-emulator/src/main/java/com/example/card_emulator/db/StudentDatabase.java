package com.example.card_emulator.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// --- UPDATE VERSION TO 2 ---
@Database(entities = {WalletEntity.class}, version = 2, exportSchema = false)
public abstract class StudentDatabase extends RoomDatabase {

    public abstract WalletDao walletDao();

    private static volatile StudentDatabase INSTANCE;

    public static StudentDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StudentDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    StudentDatabase.class, "student_card_secure.db")
                            // This wipes the old DB to prevent crashes during dev
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}