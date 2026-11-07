package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;

final class GelMiniSnapshot {
    double temperature = Double.NaN;
    boolean ramKnown, lowMemory, storageKnown;
    long availableRam, availableStorage, totalStorage;
    int health = BatteryManager.BATTERY_HEALTH_UNKNOWN;

    static GelMiniSnapshot read(Context context) {
        GelMiniSnapshot s = new GelMiniSnapshot();
        try {
            Intent battery = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (battery != null) {
                int temp = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Integer.MIN_VALUE);
                if (temp != Integer.MIN_VALUE && temp >= -400 && temp <= 1500) s.temperature = temp / 10.0;
                s.health = battery.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
            }
        } catch (RuntimeException ignored) {}
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(info);
                s.ramKnown = info.totalMem > 0 && info.availMem >= 0;
                s.lowMemory = info.lowMemory;
                s.availableRam = info.availMem;
            }
        } catch (RuntimeException ignored) {}
        try {
            StatFs fs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            s.availableStorage = fs.getAvailableBytes();
            s.totalStorage = fs.getTotalBytes();
            s.storageKnown = s.totalStorage > 0 && s.availableStorage >= 0 && s.availableStorage <= s.totalStorage;
        } catch (RuntimeException ignored) {}
        return s;
    }

    boolean complete() { return Double.isFinite(temperature) && ramKnown && storageKnown; }

    int healthLevel() {
        return health == BatteryManager.BATTERY_HEALTH_OVERHEAT
                || health == BatteryManager.BATTERY_HEALTH_DEAD
                || health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE
                || health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE
                || health == BatteryManager.BATTERY_HEALTH_COLD ? 1 : 0;
    }
}
