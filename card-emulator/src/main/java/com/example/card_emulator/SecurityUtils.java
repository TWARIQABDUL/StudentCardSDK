package com.example.card_emulator; // 👈 CORRECT PACKAGE for your SDK

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    // 1. ROOT DETECTION
    public static boolean isDeviceRooted() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
        };

        for (String path : paths) {
            if (new File(path).exists()) return true;
        }

        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    // 2. APP SIGNATURE VERIFICATION (Anti-Tamper)
    // 🔒 UPDATED: This is your specific SHA-256 key
    private static final String ALLOWED_SIGNATURE = "BC:34:13:F4:34:60:73:E5:50:C4:A8:F2:17:99:E3:06:6C:9F:65:F9:23:8B:83:15:E6:A5:A3:CF:09:15:B4:84";

    public static boolean isCallerLegitimate(Context context) {
        // In DEBUG mode, we skip this check.
        // In RELEASE mode, this blocks anyone who hasn't signed with your key.
        if (BuildConfig.DEBUG) return true;

        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {
                String sha256 = getSHA256(signature.toByteArray());

                // 🔒 COMPARE
                if (ALLOWED_SIGNATURE.equalsIgnoreCase(sha256)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static String getSHA256(byte[] sig) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(sig);
        byte[] hashText = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashText) {
            sb.append(String.format("%02X", b));
            if (sb.length() < 64 + 31) sb.append(":");
        }
        return sb.toString();
    }
}