package com.example.card_emulator;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.card_emulator.db.ScanTransaction;
import com.example.card_emulator.db.ScannerDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerManager {

    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Callback for fetching history list
    public interface HistoryCallback {
        void onHistoryLoaded(List<Map<String, Object>> history);
    }

    /**
     * LOG SALE: Saves a transaction to the local Scanner DB.
     */
    public static void saveTransaction(Context context, String name, String token, double amount, String status) {
        dbExecutor.execute(() -> {
            ScanTransaction tx = new ScanTransaction(name, token, amount, status);
            ScannerDatabase.getDatabase(context).scannerDao().insert(tx);
        });
    }

    /**
     * GET HISTORY: Returns the list of sales for the UI.
     */
    public static void getHistory(Context context, HistoryCallback callback) {
        dbExecutor.execute(() -> {
            List<ScanTransaction> rawList = ScannerDatabase.getDatabase(context).scannerDao().getAll();

            // Convert to simple Map format for Flutter
            List<Map<String, Object>> exportList = new ArrayList<>();
            for (ScanTransaction tx : rawList) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", tx.studentName);
                map.put("amount", tx.amount);
                map.put("status", tx.status);
                map.put("time", tx.timestamp);
                exportList.add(map);
            }

            mainHandler.post(() -> callback.onHistoryLoaded(exportList));
        });
    }
}