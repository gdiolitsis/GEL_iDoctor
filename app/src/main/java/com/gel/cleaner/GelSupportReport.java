package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;

/** Builds the user-reviewable email draft; never sends diagnostics automatically. */
final class GelSupportReport {
    private GelSupportReport() {}
    static String build(Context context, String userMessage) {
        StringBuilder report = new StringBuilder();
        report.append("=== GEL iDoctor Support Report ===\n\n");
        report.append("----- USER MESSAGE -----\n").append(userMessage).append("\n\n");
        report.append("----- DEVICE -----\nModel: ").append(Build.MODEL);
        report.append("\nBrand: ").append(Build.BRAND);
        report.append("\nAndroid: ").append(Build.VERSION.RELEASE);
        report.append("\nSDK: ").append(Build.VERSION.SDK_INT).append("\n\n");
        report.append("----- APP -----\n");
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            long code = Build.VERSION.SDK_INT >= 28 ? info.getLongVersionCode() : info.versionCode;
            report.append("Version: ").append(info.versionName).append(" (").append(code).append(")\n\n");
        } catch (android.content.pm.PackageManager.NameNotFoundException error) {
            report.append("Version: unavailable\n\n");
        }
        report.append("----- LOGS (recent app session) -----\n");
        String logs = GELServiceLog.getSupportLog();
        report.append(logs.trim().isEmpty() ? "No diagnostic logs recorded in this app session.\n" : logs);
        report.append("\n\n----- END OF REPORT -----\n");
        return report.toString();
    }
}
