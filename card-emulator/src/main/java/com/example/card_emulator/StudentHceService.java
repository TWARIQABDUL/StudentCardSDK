package com.example.card_emulator;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import java.nio.charset.StandardCharsets;

public class StudentHceService extends HostApduService {

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        // 1. Check if the card is enabled via our Session
        if (!CardSession.getInstance().isReady()) {
            // Return Error "6F 00" (General Error)
            return new byte[] { (byte)0x6F, (byte)0x00 };
        }

        // 2. Check if this is a SELECT command
        if (isSelectCommand(commandApdu)) {
            Log.d("HCE", "Reader connected! Sending Token...");

            // 3. Return the Token + Success Code (90 00)
            String token = CardSession.getInstance().getToken();
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
            byte[] success = new byte[]{(byte) 0x90, 0x00};

            return concat(tokenBytes, success);
        }

        // Default: Return Error "6F 00"
        return new byte[] { (byte)0x6F, (byte)0x00 };
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d("StudentHce", "Connection lost. Reason: " + reason);
    }

    // --- Helpers ---

    private boolean isSelectCommand(byte[] apdu) {
        // A standard SELECT APDU starts with 00 A4 04 00
        return apdu.length >= 4 &&
                apdu[0] == (byte)0x00 &&
                apdu[1] == (byte)0xA4 &&
                apdu[2] == (byte)0x04 &&
                apdu[3] == (byte)0x00;
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}