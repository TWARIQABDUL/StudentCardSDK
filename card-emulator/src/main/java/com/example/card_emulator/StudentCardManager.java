package com.example.card_emulator;


import android.content.Context;
import android.nfc.NfcAdapter;

public class StudentCardManager {

    /**
     * Call this from Flutter when the user logs in
     */
    public static void activateCard(String encryptedToken) {
        CardSession.getInstance().setToken(encryptedToken);
    }

    /**
     * Call this from Flutter when the user logs out
     */
    public static void deactivateCard() {
        CardSession.getInstance().disable();
    }
    public static int getNfcStatus(Context context) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(context);

        if (adapter == null) {
            return 2; // Error: No Hardware
        } else if (!adapter.isEnabled()) {
            return 1; // Error: Hardware exists, but off
        } else {
            return 0; // Success: Ready to go
        }
    }
}