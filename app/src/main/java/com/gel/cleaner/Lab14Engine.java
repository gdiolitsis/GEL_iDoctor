package com.gel.cleaner;

import android.content.Context;

public class Lab14Engine {

    private final iDoctorEngine engine;

    public Lab14Engine(Context ctx) {
        engine = iDoctorEngine.get(ctx);
    }

    // =====================================================
    // SNAPSHOT
    // =====================================================

    public static class GelBatterySnapshot {

        public int level;

        public float tempC;
        public float temperature;

        public long chargeNowMah;
        public long chargeFullMah;

        public int voltage;

        public boolean charging;
        public long cycleCount;
        public boolean rooted;

        public String source;

        public float currentMa;
    }

    public GelBatterySnapshot readSnapshot() {

        GelBatterySnapshot s = new GelBatterySnapshot();

        try {

            iDoctorEngine.BatterySnapshot b =
                    engine.readBatterySnapshotLab(); // ✅ IMPORTANT

            if (b != null) {

                s.level = b.level;

                s.chargeNowMah = b.chargeNowMah;
                s.chargeFullMah = b.chargeFullMah;

                s.tempC = b.batteryTempC;
                s.temperature = b.batteryTempC;

                s.voltage = (int) b.voltageMv;

                s.charging = b.charging;

                s.cycleCount = b.cycleCount;

                s.rooted = b.rooted;

                s.source = b.source;

                s.currentMa = b.currentMa;
            }

        } catch (Throwable ignore) {}

        return s;
    }
    
// =====================================================
// DRAIN SESSION (PRO ENGINE)
// =====================================================

public static class DrainSession {

    public long startTime;
    public long endTime;

    public long startChargeMah;
    public long endChargeMah;

    public float startVoltage;
    public float endVoltage;

    public float startTemp;
    public float endTemp;

    public boolean valid = false;
}

private DrainSession currentSession;

public void startDrainSession() {

    currentSession = new DrainSession();

    currentSession.startTime = System.currentTimeMillis();

    currentSession.startChargeMah = readChargeNowMahStable();
    currentSession.startVoltage = readVoltageStable();
    currentSession.startTemp = safeTemp();

}

public DrainSession endDrainSession() {

    if (currentSession == null)
        return null;

    currentSession.endTime = System.currentTimeMillis();

    currentSession.endChargeMah = readChargeNowMahStable();
    currentSession.endVoltage = readVoltageStable();
    currentSession.endTemp = safeTemp();

    currentSession.valid = validateSession(currentSession);

    return currentSession;
}

private float safeTemp() {

    Float t = getBatteryTemp();

    if (t == null || Float.isNaN(t))
        return Float.NaN;

    return t;
}

private boolean validateSession(DrainSession s) {

    if (s == null)
        return false;

    if (s.startChargeMah <= 0 || s.endChargeMah <= 0)
        return false;

    if (s.endChargeMah >= s.startChargeMah)
        return false;

    long dt = s.endTime - s.startTime;

    if (dt < 15000) // κάτω από 15 sec = σκουπίδι
        return false;

    return true;
}

public static class DrainResult {

    public double drainMah;
    public double mahPerHour;

    public long durationMs;

    public float voltageDrop;
    public float tempRise;

    public boolean valid;
}

public DrainResult computeDrain(DrainSession s) {

    DrainResult r = new DrainResult();

    if (s == null || !s.valid) {
        r.valid = false;
        return r;
    }

    long deltaMah = s.startChargeMah - s.endChargeMah;
    long dt = s.endTime - s.startTime;

    if (deltaMah <= 0 || dt <= 0) {
        r.valid = false;
        return r;
    }

    double hours = dt / 3600000.0;

    r.drainMah = deltaMah;
    r.mahPerHour = deltaMah / hours;

    r.durationMs = dt;

    r.voltageDrop = s.startVoltage - s.endVoltage;

    if (!Float.isNaN(s.startTemp) && !Float.isNaN(s.endTemp)) {
        r.tempRise = s.endTemp - s.startTemp;
    } else {
        r.tempRise = Float.NaN;
    }

    r.valid = true;

    return r;
}

    // =====================================================
    // STABLE READ HELPERS (LAB CRITICAL)
    // =====================================================

    public long readChargeNowMahStable() {
        return engine.readChargeNowMahStable(3, 80);
    }

    public long readChargeFullMahStable() {
        return engine.readChargeFullMahStable(3, 80);
    }

    public float readVoltageStable() {
        return engine.readBatteryVoltageMvStable(5, 60);
    }

    public float readCurrentStable() {
        return engine.readBatteryCurrentMaStable(5, 60);
    }

    public boolean isCharging() {
        return engine.isChargingNowUnified();
    }

    public Float getBatteryTemp() {
        return engine.getBatteryTempUnified();
    }

    // =====================================================
    // CONFIDENCE
    // =====================================================

    public enum ConfidenceTier {
        PRELIMINARY,
        MEDIUM,
        HIGH
    }

    public static class ConfidenceResult {

        public ConfidenceTier tier = ConfidenceTier.MEDIUM;

        public int percent = 50;

        public int validRuns = 1;
    }

    public ConfidenceResult computeConfidence() {

        // 🔧 Placeholder – logic μένει στο Activity όπως ήδη έχεις
        return new ConfidenceResult();
    }

    // =====================================================
    // AGING
    // =====================================================

    public static class AgingResult {

        public int index = 0;

        public String description = "N/A";
    }

    public AgingResult computeAging(
            double mahPerHour,
            ConfidenceResult conf,
            long duration,
            float tempStart,
            float tempEnd
    ) {

        AgingResult r = new AgingResult();

        if (mahPerHour <= 0) {
            r.index = 0;
            r.description = "No data";
            return r;
        }

        if (mahPerHour < 150) {
            r.index = 90;
            r.description = "Excellent";
        } else if (mahPerHour < 300) {
            r.index = 75;
            r.description = "Good";
        } else if (mahPerHour < 500) {
            r.index = 60;
            r.description = "Moderate wear";
        } else {
            r.index = 40;
            r.description = "Degraded battery";
        }

        return r;
    }

    // =====================================================
    // SAVE (hook only – αφήνεις Activity να τα κάνει)
    // =====================================================

    public void saveDrainValue(double v) {
        // handled in Activity (SharedPreferences)
    }

    public void saveRun() {
        // handled in Activity
    }
}
