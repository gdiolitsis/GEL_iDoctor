private void lab25CrashHistory() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 25 — GEL Ανάλυση Σφαλμάτων (ΑΥΤΟΜΑΤΗ)"
            : "LAB 25 — GEL Crash Intelligence (AUTO)");
    logLine();

    int crashCount = 0;
    int anrCount = 0;
    int systemCount = 0;

    Map<String, Integer> appEvents = new HashMap<>();
    List<String> details = new ArrayList<>();

    // ============================================================
    // (A) Android 11+ — REALTIME ERROR SNAPSHOT
    // ============================================================
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            ActivityManager am =
                    (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            if (am != null) {

                List<ActivityManager.ProcessErrorStateInfo> errs =
                        am.getProcessesInErrorState();

                if (errs != null && !errs.isEmpty()) {

                    logInfo(gr
                            ? "Στιγμιότυπο τρεχόντων σφαλμάτων"
                            : "Realtime error snapshot");

                    for (ActivityManager.ProcessErrorStateInfo e : errs) {

                        String app =
                                (e != null && e.processName != null)
                                        ? e.processName
                                        : "(unknown)";

                        appEvents.put(app, appEvents.getOrDefault(app, 0) + 1);

                        if (e.condition ==
                                ActivityManager.ProcessErrorStateInfo.CRASHED) {

                            logLabelErrorValue(
                                    "CRASH",
                                    app + " — " + safeStr(e.shortMsg)
                            );

                        } else if (e.condition ==
                                ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {

                            logLabelWarnValue(
                                    "ANR",
                                    app + " — " + safeStr(e.shortMsg)
                            );

                        } else {

                            logLabelWarnValue(
                                    gr ? "ΣΦΑΛΜΑ" : "ERROR",
                                    app + " — " + safeStr(e.shortMsg)
                            );
                        }
                    }

                    appendHtml("<br>");
                    logLabelOkValue(
                            gr ? "Σημείωση" : "Note",
                            gr
                                    ? "Το στιγμιότυπο δείχνει ΜΟΝΟ τρέχοντα crashes / ANR (όχι ιστορικό)"
                                    : "Snapshot shows ONLY current crashed / ANR processes (not history)"
                    );
                }
            }
        }

    } catch (Throwable ignore) {}

    // ============================================================
    // (B) DropBox crash logs
    // ============================================================
    try {
        DropBoxManager db =
                (DropBoxManager) getSystemService(DROPBOX_SERVICE);

        if (db != null) {

            String[] tags = {
                    "system_app_crash", "data_app_crash",
                    "system_app_anr", "data_app_anr",
                    "system_server_crash", "system_server_wtf",
                    "system_server_anr"
            };

            for (String tag : tags) {

                try {

                    long since = 0;

                    DropBoxManager.Entry ent = db.getNextEntry(tag, since);

                    // Android 13–14 / OEM workaround
                    if (ent == null) {
                        try {
                            ent = db.getNextEntry(tag, 0);
                        } catch (Throwable ignore) {}
                    }

                    int scanned = 0;

                    while (ent != null && scanned < 50) {

                        scanned++;

                        boolean crashDetected = false;

                        String ttag = tag.toLowerCase(Locale.US);

                        if (ttag.contains("system_server")) {

                            systemCount++;

                        } else if (ttag.contains("anr")) {

                            anrCount++;

                        } else if (ttag.contains("crash")
                                || ttag.contains("native_crash")
                                || ttag.contains("tombstone")) {

                            crashDetected = true;
                        }

                        String shortTxt = readDropBoxEntry(ent);

                        if (!crashDetected && shortTxt != null) {

                            String tx = shortTxt.toLowerCase(Locale.US);

                            if (tx.contains("fatal signal")
                                    || tx.contains("segmentation fault")
                                    || tx.contains("abort message")
                                    || tx.contains("signal 11")) {

                                crashDetected = true;
                            }
                        }

                        if (crashDetected) {
                            crashCount++;
                        }

                        String clean = tag.toUpperCase(Locale.US)
                                .replace("_", " ");

                        details.add(clean + ": " + shortTxt);

                        try {

                            String key;

                            if (shortTxt != null && shortTxt.length() > 0) {

                                String t = shortTxt.toLowerCase(Locale.US);
                                int pi = t.indexOf("package:");

                                if (pi >= 0) {

                                    String rest = t.substring(pi + 8).trim();
                                    String[] parts =
                                            rest.split("[\\s\\n\\r\\t]+");

                                    key = (parts.length > 0 && parts[0].contains("."))
                                            ? parts[0]
                                            : clean;

                                } else {
                                    key = clean;
                                }

                            } else {
                                key = clean;
                            }

                            appEvents.put(
                                    key,
                                    appEvents.getOrDefault(key, 0) + 1
                            );

                        } catch (Exception ignored) {}

                        try {
                            long next = ent.getTimeMillis();
                            ent = db.getNextEntry(tag, next);
                        } catch (Throwable ignore) {
                            break;
                        }
                    }

                } catch (Throwable ignorePerTag) {
                    // συνεχίζουμε στο επόμενο tag χωρίς να νεκρώνει όλο το LAB 25
                }
            }
        }

    } catch (Throwable ignored) {}

    // ============================================================
    // (C) SUMMARY + RISK SCORE
    // ============================================================
    int risk = 0;
    risk += crashCount * 5;
    risk += anrCount * 8;
    risk += systemCount * 15;

    List<String> crashPatternFindings = new ArrayList<>();
    int hwPatternScore = analyzeCrashPattern(details, crashPatternFindings);

    if (hwPatternScore > 0) {
        risk += Math.min(30, hwPatternScore / 2);
    }

    logInfo(gr
            ? "Ανάλυση μοτίβου crash"
            : "Crash pattern analysis");
    logLine();

    if (crashPatternFindings.isEmpty()) {

        logLabelOkValue(
                gr ? "Μοτίβο" : "Pattern",
                gr
                        ? "Δεν εντοπίστηκαν ενδείξεις χαμηλού επιπέδου σφαλμάτων"
                        : "No low-level crash indicators detected"
        );

    } else {

        for (String s : crashPatternFindings) {
            logWarn("• " + s);
        }

        if (hwPatternScore >= 40) {

            logLabelWarnValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Πιθανό μοτίβο χαμηλού επιπέδου σφαλμάτων (RAM / storage / kernel)"
                            : "Possible low-level fault pattern (RAM / storage / kernel)"
            );

        } else {

            logLabelWarnValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Μικρές ενδείξεις συστημικών σφαλμάτων"
                            : "Minor system-level fault indicators"
            );
        }
    }

    if (risk > 100) risk = 100;

    appendHtml("<br>");
    logInfo(gr
            ? "Έλεγχος καθυστέρησης αποθηκευτικού"
            : "Storage latency probe");
    logLine();

    long latency = storageLatencyProbe();

    if (latency < 0) {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Δεν ήταν δυνατή η μέτρηση latency"
                   : "Unable to measure storage latency"
        );

    } else {

        logLabelOkValue(
                gr ? "Latency" : "Latency",
                latency + " ms"
        );

        if (latency > 120) {

            logLabelWarnValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Αργή απόκριση αποθηκευτικού (πιθανή κόπωση NAND ή έντονη δραστηριότητα I/O)"
                            : "Slow storage response (possible NAND wear or heavy I/O)"
            );

        } else if (latency > 60) {

            logLabelWarnValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Μέτρια καθυστέρηση I/O"
                            : "Moderate I/O latency"
            );

        } else {

            logLabelOkValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Φυσιολογική απόκριση αποθηκευτικού"
                            : "Normal storage latency"
            );
        }
    }

    appendHtml("<br>");
    logInfo(gr ? "Σύνοψη Σταθερότητας" : "Stability summary");
    logLine();

    logLabelOkValue(
            gr ? "Συμβάντα Crash" : "Crash events",
            String.valueOf(crashCount)
    );

    if (anrCount > 0)
        logLabelWarnValue("ANR", String.valueOf(anrCount));
    else
        logLabelOkValue("ANR", "0");

    if (systemCount > 0)
        logLabelErrorValue(
                gr ? "Σφάλματα Συστήματος" : "System-level faults",
                String.valueOf(systemCount)
        );
    else
        logLabelOkValue(
                gr ? "Σφάλματα Συστήματος" : "System-level faults",
                "0"
        );

    appendHtml("<br>");
    logInfo(gr ? "Δείκτης Ρίσκου Σταθερότητας"
               : "Stability risk score");
    logLine();

    if (risk >= 60)
        logLabelErrorValue("Risk", risk + "%");
    else if (risk >= 30)
        logLabelWarnValue("Risk", risk + "%");
    else
        logLabelOkValue("Risk", risk + "%");

    logLabelOkValue(
        gr ? "Σημείωση" : "Note",
        gr
                ? "Η βαθμολογία βασίζεται σε διαθέσιμα system logs (διαφέρει ανά OEM / Android)"
                : "Score based on detected system log signals (availability varies by OEM / Android)"
    );

    boolean softwareCrashLikely =
            (crashCount > 0 || anrCount > 0);

    // ============================================================
    // (D) HEATMAP
    // ============================================================
    if (!appEvents.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Heatmap (συχνότερα συμβάντα)"
                : "Heatmap (top offenders)");
        logLine();

        appEvents.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(e -> {

                    String label =
                            e.getValue() + (gr ? " συμβάντα" : " events");

                    if (e.getValue() >= 10)
                        logLabelErrorValue(e.getKey(), label);
                    else if (e.getValue() >= 5)
                        logLabelWarnValue(e.getKey(), label);
                    else
                        logLabelOkValue(e.getKey(), label);
                });
    }

    // ============================================================
    // (E) FULL DETAILS
    // ============================================================
    if (!details.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Αναλυτικά αρχεία crash"
                : "Detailed crash records");
        logLine();

        int count = details.size();

        if (count == 1)
            logLabelWarnValue(
                    gr ? "Καταγραφές" : "Records",
                    gr ? "1 crash εντοπίστηκε"
                       : "1 crash detected");
        else if (count <= 3)
            logLabelWarnValue(
                    gr ? "Καταγραφές" : "Records",
                    count + (gr ? " crashes εντοπίστηκαν"
                                 : " crashes detected"));
        else
            logLabelErrorValue(
                    gr ? "Καταγραφές" : "Records",
                    count + (gr ? " crashes εντοπίστηκαν (ΥΨΗΛΗ αστάθεια)"
                                 : " crashes detected (HIGH instability)")
            );

        for (String d : details) {
            logLabelWarnValue(
                    gr ? "Λεπτομέρεια" : "Detail",
                    d
            );
        }

    } else {

        logLine();
        logLabelOkValue(
                gr ? "Ιστορικό Crash" : "Crash history",
                gr
                        ? "Δεν εντοπίστηκαν καταγραφές crash"
                        : "No crash records detected"
        );
    }

    GELServiceLog.info(
            "SUMMARY: CRASH_ORIGIN=" +
                    (softwareCrashLikely ? "SOFTWARE" : "UNCLEAR")
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 25 ολοκληρώθηκε." : "Lab 25 finished.");
    logLine();
}
