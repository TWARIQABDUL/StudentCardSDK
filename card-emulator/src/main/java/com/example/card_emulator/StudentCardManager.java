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

    // Background executor for database operations
    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    // Handler to send data back to the UI thread
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // --- STATUS CODES ---
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_DEVICE_ROOTED = 10;
    public static final int STATUS_APP_TAMPERED = 11;

    public static final int NFC_READY = 0;
    public static final int NFC_DISABLED = 1;
    public static final int NFC_MISSING = 2;

    /**
     * Activates the card logic securely.
     * Returns an integer status code instead of throwing exceptions.
     * * @return 0=Success, 10=Rooted, 11=Tampered
     */
    public static int activateCard(Context context, String token) {
        // 1. Check for Root
        if (SecurityUtils.isDeviceRooted()) {
            return STATUS_DEVICE_ROOTED;
        }

        // 2. Check for App Integrity (Anti-Cloning)
        if (!SecurityUtils.isCallerLegitimate(context)) {
            return STATUS_APP_TAMPERED;
        }

        // 3. Proceed if safe
        CardSession.getInstance().setToken(token);
        return STATUS_SUCCESS;
    }

    public static void deactivateCard() {
        CardSession.getInstance().disable();
    }

    /**
     * Checks NFC Hardware Status.
     * @return 0=Ready, 1=Off, 2=Missing
     */
    public static int getNfcStatus(Context context) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(context);
        if (adapter == null) {
            return NFC_MISSING;
        } else if (!adapter.isEnabled()) {
            return NFC_DISABLED;
        } else {
            return NFC_READY;
        }
    }

    /**
     * CACHE UPDATE (Async): Saves the balance securely in the background.
     */
    public static void saveLatestBalance(Context context, String token, double amount) {
        // Don't save sensitive financial data on rooted devices
        if (SecurityUtils.isDeviceRooted()) return;

        dbExecutor.execute(() -> {
            WalletEntity wallet = new WalletEntity(token, amount);
            StudentDatabase.getDatabase(context).walletDao().saveWallet(wallet);
        });
    }

    /**
     * OFFLINE VIEW (Async): Fetches balance from local DB without blocking UI.
     */
    public static void getCachedBalance(Context context, String token, BalanceCallback callback) {
        dbExecutor.execute(() -> {
            try {
                WalletEntity wallet = StudentDatabase.getDatabase(context).walletDao().getWallet(token);
                double balance = (wallet != null) ? wallet.balance : 0.00;

                // Send result back to Main UI Thread
                mainHandler.post(() -> callback.onBalanceLoaded(balance));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("DB Error: " + e.getMessage()));
            }
        });
    }
}