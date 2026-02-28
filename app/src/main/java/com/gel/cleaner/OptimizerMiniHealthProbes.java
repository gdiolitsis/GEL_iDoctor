package com.gel.cleaner;

import android.app.ActivityManager;
import android.app.ApplicationExitInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import java.io.RandomAccessFile;
import java.util.List;

public class OptimizerMiniHealthProbes {

    public static class Result {
        public int score = 0;
        public boolean cpuSpike = false;
        public boolean thermalHigh = false;
        public boolean crashSignal = false;
        public boolean cacheHigh = false;
    }

    public static Result run(Context ctx, boolean cacheHigh) {

        Result r = new Result();

        if (isCpuSpike()) {
            r.cpuSpike = true;
            r.score += 1;
        }

        if (isThermalHigh(ctx)) {
            r.thermalHigh = true;
            r.score += 1;
        }

        if (hasRecentSystemCrash(ctx)) {
            r.crashSignal = true;
            r.score += 2;
        }

        if (cacheHigh) {
            r.cacheHigh = true;
            r.score += 1;
        }

        return r;
    }

    private static boolean isCpuSpike() {
        try {
            long[] a = readCpu();
            Thread.sleep(300);
            long[] b = readCpu();

            long idle = b[3] - a[3];
            long total = 0;
            for (int i = 0; i < b.length; i++) {
                total += (b[i] - a[i]);
            }

            if (total <= 0) return false;

            double usage = (total - idle) * 100.0 / total;
            return usage >= 60.0;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private static long[] readCpu() throws Exception {
        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
        String[] toks = reader.readLine().split("\\s+");
        reader.close();

        long[] vals = new long[toks.length - 1];
        for (int i = 1; i < toks.length; i++) {
            vals[i - 1] = Long.parseLong(toks[i]);
        }
        return vals;
    }

    private static boolean isThermalHigh(Context ctx) {
        try {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = ctx.registerReceiver(null, iFilter);

            if (batteryStatus == null) return false;

            int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (temp <= 0) return false;

            double celsius = temp / 10.0;
            return celsius >= 43.0;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private static boolean hasRecentSystemCrash(Context ctx) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return false;

        try {
            ActivityManager am =
                    (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

            if (am == null) return false;

            List<ApplicationExitInfo> exits =
                    am.getHistoricalProcessExitReasons(null, 0, 20);

            long now = System.currentTimeMillis();

            for (ApplicationExitInfo info : exits) {

                long timestamp = info.getTimestamp();
                long diff = now - timestamp;

                if (diff > 24L * 60L * 60L * 1000L) continue;

                int reason = info.getReason();

                if (reason == ApplicationExitInfo.REASON_CRASH ||
                    reason == ApplicationExitInfo.REASON_ANR ||
                    reason == ApplicationExitInfo.REASON_LOW_MEMORY) {

                    return true;
                }
            }

        } catch (Throwable ignore) {}

        return false;
    }
}
