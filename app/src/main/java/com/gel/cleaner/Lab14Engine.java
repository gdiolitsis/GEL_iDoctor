package com.gel.cleaner;

import android.content.Context;

public class Lab14Engine {

    private final iDoctorEngine engine;

    public Lab14Engine(Context ctx) {
        engine = new iDoctorEngine(ctx);
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

    public String source;

    // REQUIRED by ManualTestsActivity

    public boolean charging;
    public long cycleCount;
    public boolean rooted;

}
    public GelBatterySnapshot readSnapshot() {

        GelBatterySnapshot s = new GelBatterySnapshot();

        try {

            iDoctorEngine.BatterySnapshot b =
                    engine.readBatterySnapshot();

            if (b != null) {

                s.level = b.level;

                try { s.tempC = 0; } catch (Throwable ignore) {}
try { s.temperature = 0; } catch (Throwable ignore) {}
try { s.voltage = 0; } catch (Throwable ignore) {}
                s.chargeNowMah = b.chargeNowMah;
                s.chargeFullMah = b.chargeFullMah;

                s.source = "iDoctorEngine";

            }

        } catch (Throwable ignore) {}

        return s;
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

    public ConfidenceTier tier = ConfidenceTier.PRELIMINARY;

    public int percent = 0;

    public int validRuns = 0;

}

    public ConfidenceResult computeConfidence() {

        ConfidenceResult r = new ConfidenceResult();

        r.percent = 50;
        r.tier = ConfidenceTier.MEDIUM;

        return r;
    }

    // =====================================================
    // AGING
    // =====================================================

    public static class AgingResult {

        public int index;
        public String description;

    }

    public AgingResult computeAging(
            double mahPerHour,
            ConfidenceResult conf,
            long duration,
            float tempStart,
            float tempEnd
    ) {

        AgingResult a = new AgingResult();

        a.index = 0;
        a.description = "N/A";

        return a;
    }

    // =====================================================
    // SAVE
    // =====================================================

    public void saveDrainValue(double v) {
    }

    public void saveRun() {
    }

}
