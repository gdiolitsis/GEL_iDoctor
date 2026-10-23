// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleSpecProvider.java — Apple Hardcoded Bridge (Reflection Safe)
// FINAL • LOCKED • PRODUCTION SAFE
// ============================================================

package com.gel.cleaner.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.gel.cleaner.iphone.AppleDeviceSpec;
import com.gel.cleaner.iphone.AppleSpecs;

import java.lang.reflect.Field;
import java.util.Locale;

public final class AppleSpecProvider {

    private AppleSpecProvider() {}

    // =========================================================
    // SELECTION DTO
    // =========================================================
    public static final class Selection {
        public final String type;   // "iphone" | "ipad"
        public final String model;  // may be null

        public Selection(String type, String model) {
            this.type = type;
            this.model = model;
        }
    }

    // =========================================================
    // READ SAVED SELECTION (NO FAKE FALLBACKS)
    // =========================================================
    public static Selection getSavedSelection(Context c) {

        SharedPreferences p =
                c.getSharedPreferences("gel_prefs", Context.MODE_PRIVATE);

        String type =
                firstNonEmpty(
                        p.getString("apple_type", null),
                        p.getString("apple_device_type", null),
                        p.getString("apple_selected_type", null),
                        p.getString("device_type", null),
                        p.getString("selected_device_type", null)
                );

        String model =
                firstNonEmpty(
                        p.getString("apple_model", null),
                        p.getString("apple_device_model", null),
                        p.getString("apple_selected_model", null),
                        p.getString("device_model", null),
                        p.getString("selected_device_model", null)
                );

        if (type == null) return new Selection("iphone", null);

        type = type.toLowerCase(Locale.US).contains("ipad")
                ? "ipad"
                : "iphone";

        // 🔒 NO SILENT FALLBACKS
        if (model == null || model.trim().isEmpty()) {
            return new Selection(type, null);
        }

        return new Selection(type, model.trim());
    }

    // =========================================================
    // REFLECTION BRIDGE → AppleSpecs / iPadSpecs
    // =========================================================
    public static Object getSpecOrNull(Context c) {
        try {
            Selection s = getSavedSelection(c);
            if (s.model == null) return null;

            return AppleSpecs.get(s.model);

        } catch (Throwable ignore) {
            return null;
        }
    }

    // =========================================================
    // DIRECT SAFE ACCESS FOR ACTIVITIES
    // =========================================================
    public static AppleDeviceSpec getSelectedDevice(Context ctx) {
        Object spec = getSpecOrNull(ctx);
        return spec instanceof AppleDeviceSpec
                ? (AppleDeviceSpec) spec
                : AppleDeviceSpec.unknown();
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private static String firstNonEmpty(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.trim().isEmpty())
                return v.trim();
        }
        return null;
    }

    // =========================================================
    // SAFE FIELD ACCESSORS
    // =========================================================
    public static String getStr(Object spec, String... fieldNames) {
        if (spec == null) return null;
        for (String f : fieldNames) {
            try {
                Field ff = spec.getClass().getDeclaredField(f);
                ff.setAccessible(true);
                Object v = ff.get(spec);
                if (v != null) {
                    String s = String.valueOf(v).trim();
                    if (!s.isEmpty()) return s;
                }
            } catch (Throwable ignore) {}
        }
        return null;
    }

    public static int getInt(Object spec, String... fieldNames) {
        if (spec == null) return -1;
        for (String f : fieldNames) {
            try {
                Field ff = spec.getClass().getDeclaredField(f);
                ff.setAccessible(true);
                Object v = ff.get(spec);
                if (v instanceof Number)
                    return ((Number) v).intValue();
            } catch (Throwable ignore) {}
        }
        return -1;
    }

    public static double getDouble(Object spec, String... fieldNames) {
        if (spec == null) return -1;
        for (String f : fieldNames) {
            try {
                Field ff = spec.getClass().getDeclaredField(f);
                ff.setAccessible(true);
                Object v = ff.get(spec);
                if (v instanceof Number)
                    return ((Number) v).doubleValue();
            } catch (Throwable ignore) {}
        }
        return -1;
    }
}
