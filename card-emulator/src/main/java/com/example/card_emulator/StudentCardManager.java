package com.example.card_emulator;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Looper;

import com.example.card_emulator.db.StudentDatabase;
import com.example.card_emulator.db.WalletEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentCardManager {

    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // --- STATUS CODES ---
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_DEVICE_ROOTED = 10;
    public static final int STATUS_APP_TAMPERED = 11;

    public static final int NFC_READY = 0;
    public static final int NFC_DISABLED = 1;
    public static final int NFC_MISSING = 2;

    // --- 1. ACTIVATE (UPDATED) ---
    public static int activateCard(Context context, String token) {
        // Basic security checks
        if (SecurityUtils.isDeviceRooted()) return STATUS_DEVICE_ROOTED;

        // Enable NFC Logic
        CardSession.getInstance().setToken(token);
        return STATUS_SUCCESS;
    }

    // --- 2. SAVE USER DATA (NEW METHOD) ---
    // Called from Flutter after Login to cache data
    public static void saveUserData(Context context, String token, String name, String email,
                                    String role, double balance, String validUntil, boolean isActive) {

        if (SecurityUtils.isDeviceRooted()) return;

        dbExecutor.execute(() -> {
            WalletEntity user = new WalletEntity(token, name, email, role, balance, validUntil, isActive);
            StudentDatabase.getDatabase(context).walletDao().saveWallet(user);
        });
    }

    // --- 3. GET CACHED USER (UPDATED) ---
    // Returns the full WalletEntity so Flutter can show the dashboard offline
    public interface UserCallback {
        void onUserLoaded(WalletEntity user);
        void onError(String msg);
    }

    public static void getCachedUser(Context context, String token, UserCallback callback) {
        dbExecutor.execute(() -> {
            try {
                WalletEntity user = StudentDatabase.getDatabase(context).walletDao().getWallet(token);
                mainHandler.post(() -> callback.onUserLoaded(user));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("DB Error: " + e.getMessage()));
            }
        });
    }

    // ... Keep deactivateCard and getNfcStatus as they were ...
    public static void deactivateCard() {
        CardSession.getInstance().disable();
    }

    public static int getNfcStatus(Context context) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(context);
        if (adapter == null) return NFC_MISSING;
        if (!adapter.isEnabled()) return NFC_DISABLED;
        return NFC_READY;
    }
}