package com.gel.cleaner;

import android.content.Context;

public class Lab14Engine {

    private final iDoctorEngine engine;

    public Lab14Engine(Context ctx) {
        engine = new iDoctorEngine(ctx);
    }

    // --------------------------------------------------
    // SNAPSHOT
    // --------------------------------------------------

    public static class GelBatterySnapshot {
        public int level;
        public float tempC;
        public int voltage;
    }

    public GelBatterySnapshot readSnapshot() {

        GelBatterySnapshot s = new GelBatterySnapshot();

        try {

            iDoctorEngine.BatterySnapshot b =
                    engine.readBatterySnapshot();

            if (b != null) {
                s.level = b.level;
                s.tempC = b.tempC;
                s.voltage = b.voltage;
            }

        } catch (Throwable ignore) {}

        return s;
    }

    // --------------------------------------------------
    // CONFIDENCE
    // --------------------------------------------------

    public enum ConfidenceTier {
        PRELIMINARY,
        MEDIUM,
        HIGH
    }

    public static class ConfidenceResult {
        public ConfidenceTier tier = ConfidenceTier.PRELIMINARY;
    }

    public ConfidenceResult getConfidence() {

        ConfidenceResult r = new ConfidenceResult();

        r.tier = ConfidenceTier.MEDIUM;

        return r;
    }

    // --------------------------------------------------
    // AGING
    // --------------------------------------------------

    public static class AgingResult {
        public int percent;
    }

    public AgingResult getAging() {

        AgingResult a = new AgingResult();
        a.percent = 0;

        return a;
    }

}
