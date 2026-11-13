package com.gel.cleaner;

/** Typed report severity for diagnostics. Neutral format stays unchanged. */
public final class GelDiagnosticSeverity {
    public enum Level { NEUTRAL, WARNING, ERROR }

    private GelDiagnosticSeverity() {}

    public static String plainLine(Level level, String label, String value) {
        String prefix = level == Level.WARNING ? "⚠ "
                : level == Level.ERROR ? "✖ " : "";
        return prefix + label + ": " + value;
    }
}
