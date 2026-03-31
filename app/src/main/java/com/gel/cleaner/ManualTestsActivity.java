logLine();

// ======================
// BATTERY LIFE ESTIMATION
// ======================

appendHtml("<br>");
logLine();

logOk(gr
        ? "Εκτίμηση διάρκειας μπαταρίας"
        : "Estimated battery life");

logLine();

if (startMah > 0 && endMah > 0 && endMah < startMah) {

    long deltaMah = startMah - endMah;

    float testHours = 5f / 60f;

    float mahPerHour = deltaMah / testHours;

    if (baselineFullMah > 0 && mahPerHour > 0) {

        float estimatedHours = baselineFullMah / mahPerHour;

        logLabelOkValue(
                gr ? "Εκτιμώμενη διάρκεια" : "Estimated duration",
                String.format(Locale.US, "%.1f ώρες", estimatedHours)
        );

    } else {

        logWarn(gr
                ? "Αδυναμία εκτίμησης διάρκειας"
                : "Cannot estimate battery life");
    }

} else {

    logWarn(gr
            ? "Μη έγκυρη κατανάλωση για εκτίμηση"
            : "Invalid drain for estimation");
}

logLine();

// 🔚 ΤΕΛΟΣ
isLab14BMode = false;
