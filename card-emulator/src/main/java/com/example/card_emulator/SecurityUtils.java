package com.example.card_emulator;

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
    // You must replace this with YOUR real Release Keystore SHA-256 hash.
    // Run `keytool -list -v -keystore your-key.jks` to get it.
    private static final String ALLOWED_SIGNATURE = "A1:B2:C3:D4:E5..."; // <--- REPLACE THIS

    public static boolean isCallerLegitimate(Context context) {
        // In DEBUG mode, we skip this check so you can test easily.
        // In RELEASE mode, this is critical.
        if (BuildConfig.DEBUG) return true;

        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {
                String sha256 = getSHA256(signature.toByteArray());
                if (ALLOWED_SIGNATURE.equals(sha256)) {
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
            if (sb.length() < 64 + 31) sb.append(":"); // Add colons
        }
        return sb.toString();
    }
}