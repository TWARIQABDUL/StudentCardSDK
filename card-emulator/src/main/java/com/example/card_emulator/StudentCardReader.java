package com.example.card_emulator;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

public class StudentCardReader implements NfcAdapter.ReaderCallback {

    // Config: The AID of the Student Card we are looking for
    private static final byte[] STUDENT_AID = {
            (byte)0xF0, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06
    };

    private static final byte[] SELECT_HEADER = {
            (byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x00
    };

    private WeakReference<Activity> activityRef;
    private StudentCardCallback callback;

    public StudentCardReader(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    /**
     * Starts listening for the Student Card.
     * @param callback The listener that will receive the result.
     */
    public void startScanning(StudentCardCallback callback) {
        this.callback = callback;
        Activity activity = activityRef.get();

        if (activity != null) {
            NfcAdapter adapter = NfcAdapter.getDefaultAdapter(activity);
            if (adapter != null && adapter.isEnabled()) {
                Bundle options = new Bundle();
                options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

                // Enable Reader Mode
                adapter.enableReaderMode(
                        activity,
                        this,
                        NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                        options
                );
            } else {
                callback.onScanError("NFC is not enabled or supported");
            }
        }
    }

    /**
     * Stops the reader mode. Call this when the activity pauses or you are done.
     */
    public void stopScanning() {
        Activity activity = activityRef.get();
        if (activity != null) {
            NfcAdapter adapter = NfcAdapter.getDefaultAdapter(activity);
            if (adapter != null) {
                adapter.disableReaderMode(activity);
            }
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep != null) {
            try {
                isoDep.connect();

                // 1. Construct the Command: [HEADER] + [LENGTH] + [AID]
                byte[] command = new byte[SELECT_HEADER.length + 1 + STUDENT_AID.length];
                System.arraycopy(SELECT_HEADER, 0, command, 0, SELECT_HEADER.length);
                command[4] = (byte) STUDENT_AID.length;
                System.arraycopy(STUDENT_AID, 0, command, 5, STUDENT_AID.length);

                // 2. Transceive
                byte[] response = isoDep.transceive(command);

                // 3. Validate Response (Must end in 90 00)
                int len = response.length;
                if (len >= 2 && response[len-2] == (byte)0x90 && response[len-1] == (byte)0x00) {
                    // Extract payload
                    String token = new String(response, 0, len - 2, StandardCharsets.UTF_8);
                    sendSuccess(token);
                } else {
                    sendError("Invalid Card Response");
                }

                isoDep.close();

            } catch (IOException e) {
                sendError("NFC Connection Lost");
            }
        }
    }

    // Helper to send results back to the Main Thread
    private void sendSuccess(String data) {
        Activity activity = activityRef.get();
        if (activity != null && callback != null) {
            activity.runOnUiThread(() -> callback.onScanSuccess(data));
        }
    }

    private void sendError(String msg) {
        Activity activity = activityRef.get();
        if (activity != null && callback != null) {
            activity.runOnUiThread(() -> callback.onScanError(msg));
        }
    }
}