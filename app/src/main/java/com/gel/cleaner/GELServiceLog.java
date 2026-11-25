// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELServiceLog — Stable Edition (PLAIN + HTML)
// ============================================================

package com.gel.cleaner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class GELServiceLog {

    // ----------------------------
    // BUFFERS
    // ----------------------------
    private static final StringBuilder SUPPORT = new StringBuilder(4096); // bounded current-process support history
    private static final StringBuilder LOG  = new StringBuilder(4096); // plain text
    private static final StringBuilder HTML = new StringBuilder(4096); // colored html

    /**
     * Ordered PDF value-color evidence, kept in lockstep with LOG.
     * Plain log text remains unchanged for all consumers (including Lab 29).
     */
    public static final class PdfRow {
        public final String text;
        public final String valueColor;
        // Exact value offset for iPhone labels that do not use a colon.
        // -1 means legacy label:value rendering (Android labs).
        public final int valueStart;
        private PdfRow(String text, String valueColor, int valueStart) {
            this.text = text;
            this.valueColor = valueColor;
            this.valueStart = valueStart;
        }
    }
    /**
     * Creates a detached, explicitly sourced row for remote Service Reports.
     * Does not modify the local static service log on the technician device.
     */
    public static PdfRow detachedPdfRow(String text, String color, int start) {
        String safeText = text == null ? "" : text.replace("\\r", " ").replace("\\n", " ").trim();
        String safeColor = color != null && color.matches("#[0-9A-Fa-f]{6}")
                ? color : null;
        int safeStart = start >= 0 && start < safeText.length() ? start : -1;
        return new PdfRow(safeText, safeColor, safeStart);
    }

    private static final ArrayList<PdfRow> PDF_ROWS = new ArrayList<>();

    private static final SimpleDateFormat TS =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private static final int MAX_CHARS = 50000;

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================
    private static synchronized void addPlain(String line) {
        if (line == null) return;
        LOG.append(line).append('\n');
        PDF_ROWS.add(new PdfRow(line, null, -1));
        SUPPORT.append(line).append('\n');
        if (SUPPORT.length() > MAX_CHARS) SUPPORT.delete(0, SUPPORT.length() - MAX_CHARS);
        ensureLimit();
    }

    private static synchronized void addHtml(String htmlLine) {
        if (htmlLine == null) return;
        HTML.append(htmlLine).append("<br>");
        ensureLimit();
    }

    private static String sanitize(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").trim();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static void ensureLimit() {
        if (LOG.length() > MAX_CHARS) {
            int cut = (int) (MAX_CHARS * 0.20);
            LOG.delete(0, cut);
            trimPdfRowsFromHead(cut);
        }
        if (HTML.length() > MAX_CHARS) {
            int cut = (int) (MAX_CHARS * 0.20);
            HTML.delete(0, cut);
        }
    }

    // Advance the styled row snapshot by the same number of characters
    // truncated from the oldest end of the plain-text service log.
    private static void trimPdfRowsFromHead(int chars) {
        while (chars > 0 && !PDF_ROWS.isEmpty()) {
            PdfRow first = PDF_ROWS.get(0);
            int lineChars = first.text.length() + 1; // LOG appends one newline
            if (chars >= lineChars) {
                chars -= lineChars;
                PDF_ROWS.remove(0);
            } else {
                PDF_ROWS.set(0, new PdfRow(first.text.substring(chars), null, -1));
                chars = 0;
            }
        }
    }

    /**
     * Preserve a screen log's actual value color without putting markup in
     * the plain-text diagnostics that other laboratories use as evidence.
     */
    public static synchronized void addStyledValue(String plainLine, String valueColor) {
        addStyledValue(plainLine, valueColor, -1);
    }

    /**
     * The optional offset identifies the start of the VALUE after a label,
     * including iPhone entries that historically used "ℹ Label Value" rather
     * than "Label: Value". PDF rendering never guesses from diagnostic text.
     */
    public static synchronized void addStyledValue(
            String plainLine, String valueColor, int valueStart) {
        if (plainLine == null || plainLine.trim().isEmpty()) return;
        String clean = sanitize(plainLine);
        addPlain(clean);
        String color = valueColor != null && valueColor.matches("#[0-9A-Fa-f]{6}")
                ? valueColor : null;
        int safeOffset = valueStart >= 0 && valueStart < clean.length()
                ? valueStart : -1;
        int last = PDF_ROWS.size() - 1;
        if (last >= 0 && PDF_ROWS.get(last).text.equals(clean)) {
            PDF_ROWS.set(last, new PdfRow(clean, color, safeOffset));
        }
        // Preserve existing in-app HTML compatibility; the printable palette
        // is calculated independently for contrast against white paper.
        addHtml("<font color='#FFFFFF'>" + escape(clean) + "</font>");
    }

    /**
     * iPhone Labs retain the PRECISE existing plain report text, including
     * informational / warning / error markers. Only the PDF's field value
     * receives the color recorded on screen; its label remains neutral.
     */
    private static synchronized void addIPhoneLabelValue(
            String prefix, String label, String value, String valueColor) {
        String formatted = sanitize(label + " " + value);
        String cleanValue = sanitize(value);
        String line = prefix + formatted;
        int valueOffset = cleanValue.isEmpty() ? -1
                : line.length() - cleanValue.length();
        addStyledValue(line, valueColor, valueOffset);
    }

    public static synchronized void iPhoneInfoValue(
            String label, String value, String valueColor) {
        addIPhoneLabelValue("ℹ ", label, value, valueColor);
    }

    public static synchronized void iPhoneWarnValue(
            String label, String value, String valueColor) {
        addIPhoneLabelValue("⚠ ", label, value, valueColor);
    }

    public static synchronized void iPhoneErrorValue(
            String label, String value, String valueColor) {
        addIPhoneLabelValue("✖ ", label, value, valueColor);
    }

    public static synchronized List<PdfRow> getPdfRows() {
        return new ArrayList<>(PDF_ROWS);
    }

    // ============================================================
    // PUBLIC LOGGING API (SYMBOLS ONLY)
    // ============================================================
    public static synchronized void info(String msg)  {
        String m = sanitize(msg);
        addPlain("ℹ " + m);
        addHtml("<font color='#7FC8FF'>ℹ " + escape(m) + "</font>");
    }

    public static synchronized void ok(String msg)    {
        String m = sanitize(msg);
        addPlain("✔ " + m);
        addHtml("<font color='#39FF14'>✔ " + escape(m) + "</font>");
    }

    public static synchronized void warn(String msg)  {
        String m = sanitize(msg);
        addPlain("⚠ " + m);
        addHtml("<font color='#FFD966'>⚠ " + escape(m) + "</font>");
    }

    public static synchronized void error(String msg) {
        String m = sanitize(msg);
        addPlain("✖ " + m);
        addHtml("<font color='#FF5555'>✖ " + escape(m) + "</font>");
    }

    // ============================================================
    // SEPARATOR LINE
    // ============================================================
    public static synchronized void line() {
        String sep = "--------------------------------------------";
        addPlain(sep);
        addHtml(sep);
    }

    // ============================================================
    // SECTION HEADER
    // ============================================================
    public static synchronized void section(String title) {
        if (title == null || title.trim().isEmpty())
            title = "SECTION";

        String t = title.toUpperCase(Locale.US);
        String sep = "--------------------------------------------";

        addPlain(sep);
        addPlain(t);
        addPlain(sep);

        addHtml(sep);
        addHtml("<b>" + escape(t) + "</b>");
        addHtml(sep);
    }

    // ============================================================
    // LAB FINISHED BLOCK  ⭐ ΜΟΝΟ ΕΔΩ μπαίνει το κενό ⭐
    // ============================================================
    public static synchronized void labFinished(String labName) {
        if (labName == null || labName.trim().isEmpty())
            labName = "Lab";

        ok(labName + " finished.");
        line();

        // ---- ΕΝΑ ΚΕΝΟ ΜΕΤΑ ΤΟ LAB ----
        addPlain("");
        addHtmlRaw("<br>");
    }

    // ============================================================
    // RAW HTML (no auto <br>)
    // ============================================================
    private static synchronized void addHtmlRaw(String html) {
        if (html == null) return;
        HTML.append(html);
        ensureLimit();
    }

    // ============================================================
    // GETTERS
    // ============================================================
    public static synchronized String getAll() {
        return LOG.toString();   // plain text
    }

    /** Diagnostic history survives report/lab cleanup; retained only in this app process. */
    public static synchronized String getSupportLog() {
        return SUPPORT.toString();
    }

    public static synchronized String getHtml() {
        return HTML.toString();  // colored html
    }

    // ============================================================
    // CLEAR
    // ============================================================
    public static synchronized void clear() {
        LOG.setLength(0);
        HTML.setLength(0);
        PDF_ROWS.clear();
    }

    // ============================================================
    // CHECK EMPTY
    // ============================================================
    public static synchronized boolean isEmpty() {
        return LOG.length() == 0;
    }

// ============================================================
// UNIVERSAL ADD (for PDF sync / external usage)
// ============================================================
public static synchronized void add(String line) {
    if (line == null || line.trim().isEmpty()) return;

    String clean = sanitize(line);

    // plain
    addPlain(clean);

    // html (neutral white)
    addHtml("<font color='#FFFFFF'>" + escape(clean) + "</font>");
}

    // ============================================================
    // BACKWARD COMPATIBILITY
    // ============================================================
    public static void logInfo(String msg)  { info(msg); }
    public static void logOk(String msg)    { ok(msg); }
    public static void logWarn(String msg)  { warn(msg); }
    public static void logError(String msg) { error(msg); }
    public static void logLine()            { line(); }
}
