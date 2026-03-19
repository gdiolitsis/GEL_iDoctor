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

                s.tempC = b.temperature;
                s.temperature = b.temperature;

                s.voltage = b.voltage;

                s.charging = b.charging;

                s.cycleCount = b.cycleCount;

                s.rooted = engine.isRoot();

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

        try {

            r.percent = engine.getConfidencePercent();

            r.validRuns = engine.getValidRuns();

            if (r.percent >= 70)
                r.tier = ConfidenceTier.HIGH;
            else if (r.percent >= 40)
                r.tier = ConfidenceTier.MEDIUM;
            else
                r.tier = ConfidenceTier.PRELIMINARY;

        } catch (Throwable ignore) {}

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

        try {

            a.index = engine.computeAgingIndex(
                    mahPerHour,
                    duration,
                    tempStart,
                    tempEnd
            );

            a.description = engine.getAgingDescription();

        } catch (Throwable ignore) {}

        return a;
    }

    // =====================================================
    // SAVE
    // =====================================================

    public void saveDrainValue(double v) {
        try {
            engine.saveDrain(v);
        } catch (Throwable ignore) {}
    }

    public void saveRun() {
        try {
            engine.saveRun();
        } catch (Throwable ignore) {}
    }

}
