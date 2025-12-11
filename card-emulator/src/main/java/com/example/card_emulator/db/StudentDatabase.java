package com.example.card_emulator.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WalletEntity.class}, version = 1, exportSchema = false)
public abstract class StudentDatabase extends RoomDatabase {

    public abstract WalletDao walletDao();

    private static volatile StudentDatabase INSTANCE;

    public static StudentDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StudentDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    StudentDatabase.class, "student_card_secure.db")
                            // REMOVED: .allowMainThreadQueries() -> Now it is safe!
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}