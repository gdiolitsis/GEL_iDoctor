package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Customer-side allowlisted remote command executor.
 *
 * There is intentionally NO arbitrary-code execution.
 * Only explicitly supported action names can reach device APIs.
 */
public final class GELRemoteCommandExecutor {

    public static final String ACTION_REMOTE_LANGUAGE_CHANGED =
            "com.gel.cleaner.ACTION_REMOTE_LANGUAGE_CHANGED";

    public static final class Result {

        public final boolean success;
        public final String message;
        public final Map<String, Object> data;

        Result(
                boolean success,
                String message,
                Map<String, Object> data
        ) {
            this.success = success;
            this.message = message;
            this.data = data != null
                    ? data
                    : new HashMap<>();
        }

        static Result ok(
                String message,
                Map<String, Object> data
        ) {
            return new Result(
                    true,
                    message,
                    data
            );
        }

        static Result fail(
                String message
        ) {
            return new Result(
                    false,
                    message,
                    new HashMap<>()
            );
        }
    }

    private GELRemoteCommandExecutor() {}

    public static Result execute(
            Context context,
            String action,
            Map<String, Object> payload
    ) {

        if (context == null ||
                action == null) {

            return Result.fail(
                    "Invalid remote command."
            );
        }

        switch (action) {

            case "PING":
                return ping(
                        context
                );

            case "GET_IDOCTOR_SETTINGS":
                return getSettings(
                        context
                );

            case "APPLY_IDOCTOR_SETTINGS":
                return applySettings(
                        context,
                        payload
                );

            case "SET_LANGUAGE":
                return setLanguage(
                        context,
                        payload
                );

            case "SET_PLATFORM":
                return setPlatform(
                        context,
                        payload
                );

            case "CLEAN_IDOCTOR_CACHE":
                return cleanOwnCache(
                        context
                );

            case "GET_DEVICE_SUMMARY":
                return getDeviceSummary(
                        context
                );

            case "CPU_RAM_SNAPSHOT":
                return getCpuRamSnapshot(
                        context
                );

            default:
                return Result.fail(
                        "Unsupported remote action."
                );
        }
    }

    private static Result ping(
            Context context
    ) {

        Map<String, Object> out =
                new HashMap<>();

        out.put(
                "manufacturer",
                Build.MANUFACTURER
        );

        out.put(
                "model",
                Build.MODEL
        );

        out.put(
                "sdk",
                Build.VERSION.SDK_INT
        );

        out.put(
                "language",
                LocaleHelper.getLang(
                        context
                )
        );

        out.put(
                "timestamp",
                System.currentTimeMillis()
        );

        return Result.ok(
                "Customer iDoctor responded.",
                out
        );
    }

    private static Result getSettings(
            Context context
    ) {

        Map<String, Object> out =
                new HashMap<>();

        SharedPreferences sp =
                context.getSharedPreferences(
                        "gel_prefs",
                        Context.MODE_PRIVATE
                );

        out.put(
                "language",
                LocaleHelper.getLang(
                        context
                )
        );

        out.put(
                "platform",
                sp.getString(
                        "platform_mode",
                        "android"
                )
        );

        out.put(
                "pulseEnabled",
                sp.getBoolean(
                        "pulse_enabled",
                        false
                )
        );

        boolean reminder =
                OptimizerScheduler
                        .isReminderEnabled(
                                context
                        );

        out.put(
                "optimizerReminderEnabled",
                reminder
        );

        out.put(
                "optimizerReminderDays",
                OptimizerScheduler
                        .getReminderDays(
                                context
                        )
        );

        return Result.ok(
                "iDoctor settings loaded.",
                out
        );
    }

    private static Result applySettings(
            Context context,
            Map<String, Object> payload
    ) {

        if (payload == null) {
            return Result.fail(
                    "Settings payload missing."
            );
        }

        SharedPreferences sp =
                context.getSharedPreferences(
                        "gel_prefs",
                        Context.MODE_PRIVATE
                );

        Object langRaw =
                payload.get(
                        "language"
                );

        if (langRaw instanceof String) {

            String lang =
                    (String) langRaw;

            if ("el".equals(lang) ||
                    "en".equals(lang)) {

                LocaleHelper.set(
                        context,
                        lang
                );

                notifyLanguageChanged(
                        context
                );
            }
        }

        Object pulseRaw =
                payload.get(
                        "pulseEnabled"
                );

        if (pulseRaw instanceof Boolean) {

            boolean pulse =
                    (Boolean) pulseRaw;

            sp.edit()
                    .putBoolean(
                            "pulse_enabled",
                            pulse
                    )
                    .apply();

            if (pulse) {
                OptimizerMiniPulseScheduler
                        .enable(
                                context
                        );
            } else {
                OptimizerMiniPulseScheduler
                        .disable(
                                context
                        );
            }
        }

        Object reminderRaw =
                payload.get(
                        "optimizerReminderEnabled"
                );

        boolean reminder =
                reminderRaw instanceof Boolean &&
                        (Boolean) reminderRaw;

        int days =
                7;

        Object daysRaw =
                payload.get(
                        "optimizerReminderDays"
                );

        if (daysRaw instanceof Number) {

            int candidate =
                    ((Number) daysRaw)
                            .intValue();

            if (candidate == 1 ||
                    candidate == 7 ||
                    candidate == 30) {

                days =
                        candidate;
            }
        }

        if (reminder) {

            OptimizerScheduler
                    .enableReminder(
                            context,
                            days
                    );

        } else {

            OptimizerScheduler
                    .disableReminder(
                            context
                    );
        }

        return getSettings(
                context
        );
    }

    private static Result setLanguage(
            Context context,
            Map<String, Object> payload
    ) {

        Object raw =
                payload != null
                        ? payload.get("language")
                        : null;

        if (!(raw instanceof String)) {
            return Result.fail(
                    "Language is required."
            );
        }

        String lang =
                (String) raw;

        if (!"el".equals(lang) &&
                !"en".equals(lang)) {

            return Result.fail(
                    "Unsupported language."
            );
        }

        LocaleHelper.set(
                context,
                lang
        );

        notifyLanguageChanged(
                context
        );

        Map<String, Object> out =
                new HashMap<>();

        out.put(
                "language",
                lang
        );

        out.put(
                "appliesOnNextActivityRecreation",
                true
        );

        return Result.ok(
                "Customer iDoctor language updated.",
                out
        );
    }

    private static Result setPlatform(
            Context context,
            Map<String, Object> payload
    ) {

        Object raw =
                payload != null
                        ? payload.get("platform")
                        : null;

        if (!(raw instanceof String)) {
            return Result.fail(
                    "Platform is required."
            );
        }

        String platform =
                (String) raw;

        if (!"android".equals(platform) &&
                !"apple".equals(platform)) {

            return Result.fail(
                    "Unsupported platform."
            );
        }

        context
                .getSharedPreferences(
                        "gel_prefs",
                        Context.MODE_PRIVATE
                )
                .edit()
                .putString(
                        "platform_mode",
                        platform
                )
                .apply();

        Map<String, Object> out =
                new HashMap<>();

        out.put(
                "platform",
                platform
        );

        return Result.ok(
                "Customer iDoctor platform mode updated.",
                out
        );
    }

    private static Result cleanOwnCache(
            Context context
    ) {

        long before =
                folderSize(
                        context.getCacheDir()
                ) +
                folderSize(
                        context.getExternalCacheDir()
                );

        deleteContents(
                context.getCacheDir()
        );

        deleteContents(
                context.getExternalCacheDir()
        );

        long after =
                folderSize(
                        context.getCacheDir()
                ) +
                folderSize(
                        context.getExternalCacheDir()
                );

        long cleaned =
                Math.max(
                        0L,
                        before - after
                );

        Map<String, Object> out =
                new HashMap<>();

        out.put(
                "bytesBefore",
                before
        );

        out.put(
                "bytesAfter",
                after
        );

        out.put(
                "bytesCleaned",
                cleaned
        );

        return Result.ok(
                "iDoctor cache cleaned on customer device.",
                out
        );
    }

    private static Result getDeviceSummary(
            Context context
    ) {

        Map<String, Object> out =
                new HashMap<>();

        out.put(
                "manufacturer",
                Build.MANUFACTURER
        );

        out.put(
                "brand",
                Build.BRAND
        );

        out.put(
                "model",
                Build.MODEL
        );

        out.put(
                "device",
                Build.DEVICE
        );

        out.put(
                "androidRelease",
                Build.VERSION.RELEASE
        );

        out.put(
                "sdk",
                Build.VERSION.SDK_INT
        );

        out.put(
                "language",
                LocaleHelper.getLang(
                        context
                )
        );

        try {

            BatteryManager bm =
                    (BatteryManager)
                            context.getSystemService(
                                    Context.BATTERY_SERVICE
                            );

            if (bm != null) {

                int level =
                        bm.getIntProperty(
                                BatteryManager.BATTERY_PROPERTY_CAPACITY
                        );

                if (level >= 0) {
                    out.put(
                            "batteryPercent",
                            level
                    );
                }
            }

        } catch (Throwable ignore) {}

        try {

            StatFs stat =
                    new StatFs(
                            Environment
                                    .getDataDirectory()
                                    .getAbsolutePath()
                    );

            out.put(
                    "storageTotalBytes",
                    stat.getTotalBytes()
            );

            out.put(
                    "storageFreeBytes",
                    stat.getAvailableBytes()
            );

        } catch (Throwable ignore) {}

        Result ram =
                getCpuRamSnapshot(
                        context
                );

        if (ram.success) {
            out.putAll(
                    ram.data
            );
        }

        return Result.ok(
                "Customer device summary loaded.",
                out
        );
    }

    private static Result getCpuRamSnapshot(
            Context context
    ) {

        Map<String, Object> out =
                new HashMap<>();

        out.put(
                "cpuCores",
                Runtime
                        .getRuntime()
                        .availableProcessors()
        );

        out.put(
                "javaHeapMaxBytes",
                Runtime
                        .getRuntime()
                        .maxMemory()
        );

        try {

            ActivityManager am =
                    (ActivityManager)
                            context.getSystemService(
                                    Context.ACTIVITY_SERVICE
                            );

            if (am != null) {

                ActivityManager.MemoryInfo mi =
                        new ActivityManager.MemoryInfo();

                am.getMemoryInfo(
                        mi
                );

                out.put(
                        "ramAvailableBytes",
                        mi.availMem
                );

                out.put(
                        "ramTotalBytes",
                        mi.totalMem
                );

                out.put(
                        "lowMemory",
                        mi.lowMemory
                );
            }

        } catch (Throwable ignore) {}

        return Result.ok(
                "CPU/RAM snapshot loaded.",
                out
        );
    }

    private static void notifyLanguageChanged(
            Context context
    ) {

        try {

            Intent intent =
                    new Intent(
                            ACTION_REMOTE_LANGUAGE_CHANGED
                    );

            intent.setPackage(
                    context.getPackageName()
            );

            context.sendBroadcast(
                    intent
            );

        } catch (Throwable ignore) {}
    }

    private static long folderSize(
            File file
    ) {

        if (file == null ||
                !file.exists()) {
            return 0L;
        }

        if (file.isFile()) {
            return Math.max(
                    0L,
                    file.length()
            );
        }

        long total =
                0L;

        File[] files =
                file.listFiles();

        if (files == null) {
            return 0L;
        }

        for (File child : files) {
            total += folderSize(child);
        }

        return total;
    }

    private static void deleteContents(
            File dir
    ) {

        if (dir == null ||
                !dir.exists()) {
            return;
        }

        File[] files =
                dir.listFiles();

        if (files == null) {
            return;
        }

        for (File child : files) {

            if (child.isDirectory()) {
                deleteContents(
                        child
                );
            }

            try {
                child.delete();
            } catch (Throwable ignore) {}
        }
    }
}
