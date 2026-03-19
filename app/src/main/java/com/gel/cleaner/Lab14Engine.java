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

        public boolean charging;
        public long cycleCount;
        public boolean rooted;

        public String source;
    }

    public GelBatterySnapshot readSnapshot() {

        GelBatterySnapshot s = new GelBatterySnapshot();

        try {

            iDoctorEngine.BatterySnapshot b =
                    engine.readBatterySnapshot();

            if (b != null) {

                s.level = b.level;

                s.chargeNowMah = b.chargeNowMah;
                s.chargeFullMah = b.chargeFullMah;

                s.tempC = 0;
                s.temperature = 0;

                s.voltage = 0;

                s.charging = b.charging;

                s.cycleCount = 0;

                s.rooted = false;

                s.source = "engine";

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

        public ConfidenceTier tier = ConfidenceTier.MEDIUM;

        public int percent = 50;

        public int validRuns = 1;
    }

    public ConfidenceResult computeConfidence() {

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

        return new AgingResult();
    }

    // =====================================================
    // SAVE
    // =====================================================

    public void saveDrainValue(double v) {
    }

    public void saveRun() {
    }

}
