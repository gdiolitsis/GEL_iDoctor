// GDiolitsis Engine Lab (GEL) — Author & Developer
// IPSPanicParser.java — iOS Panic Log Signature Parser (Multi-Match)
// FINAL • NO AI • RULE-BASED • SERVICE-GRADE • HARDENED

package com.gel.cleaner;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class IPSPanicParser {

    // ============================================================
    // RESULT MODEL (Primary + Secondary)
    // ============================================================
    public static class Result {
        public Result primary;
        public List<Result> secondary = new ArrayList<>();

        public String patternId;
        public String domain;
        public String cause;
        public String severity;
        public String confidence;
        public String recommendation;
    }

    // ============================================================
    // ENTRY POINT
    // ============================================================
    public static Result analyze(Context ctx, String panicText) {

        if (!ApplePanicEvidencePolicy.hasPanicEvidence(panicText))
            return null;

        try {

            JSONObject root = loadPatterns(ctx);
            JSONArray patterns = root.getJSONArray("patterns");

            List<Result> matches = new ArrayList<>();

            for (int i = 0; i < patterns.length(); i++) {

                JSONObject p = patterns.getJSONObject(i);
                JSONArray tokens = p.getJSONArray("match");

                List<String> ruleTokens = new ArrayList<>();
                for (int t = 0; t < tokens.length(); t++)
                    ruleTokens.add(tokens.optString(t, ""));

                // A phrase split across two different .ips files is NOT a
                // matched signature in either incident.
                if (!ApplePanicEvidencePolicy.tokensMatchOnePanic(panicText, ruleTokens))
                    continue;

                Result r = new Result();
                r.patternId      = p.optString("id", "unknown");
                r.domain         = p.optString("domain", "Unknown");
                // Signature match is an uncalibrated keyword hypothesis,
                // not a hardware fault or evidence of actual repair necessity.
                r.cause          = ApplePanicEvidencePolicy.possibleCause(
                        p.optString("cause", "Unknown"));
                r.severity       = "UNVERIFIED";
                r.confidence     = ApplePanicEvidencePolicy.patternConfidence();
                r.recommendation = ApplePanicEvidencePolicy.recommendation(false);

                matches.add(r);
            }

            if (matches.isEmpty()) return null;

            // Reference-pack severity was not physically validated.
            // Preserve deterministic source order, not invented risk priority.

            Result out = new Result();
            out.primary = matches.get(0);

            for (int i = 1; i < matches.size(); i++) {
                out.secondary.add(matches.get(i));
            }

            return out;

        } catch (Throwable ignore) {
            return null;
        }
    }

    // ============================================================
    // SEVERITY RANKING
    // ============================================================
    private static int severityRank(String s) {
        if (s == null) return 0;
        switch (s.toUpperCase(Locale.US)) {
            case "CRITICAL": return 4;
            case "HIGH":     return 3;
            case "MEDIUM":   return 2;
            case "LOW":      return 1;
            default:         return 0;
        }
    }

    // ============================================================
    // LOAD JSON PATTERNS (SAFE STREAM READ)
    // ============================================================
    private static JSONObject loadPatterns(Context ctx) throws Exception {

        InputStream is = ctx.getAssets().open("ips_panic_patterns.json");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;

        while ((read = is.read(buffer)) != -1) {
            bos.write(buffer, 0, read);
        }

        is.close();

        String json = new String(bos.toByteArray(), "UTF-8");
        return new JSONObject(json);
    }
}
