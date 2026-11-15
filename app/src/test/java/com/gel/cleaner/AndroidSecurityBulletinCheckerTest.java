package com.gel.cleaner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;

import org.junit.Test;

public class AndroidSecurityBulletinCheckerTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 15);

    private static String row(String link, String dates) {
        return "<tr><td><a href=\"" + link + "\">November 2026</a></td>"
                + "<td>November 2, 2026</td><td>" + dates + "</td></tr>";
    }

    @Test
    public void choosesHighestPatchLevelFromOfficialBulletinRow() {
        String html = "<table><tbody>"
                + row("/docs/security/bulletin/2026/2026-11-01",
                        "2026-11-01<br>2026-11-05")
                + row("/docs/security/bulletin/2026/2026-10-01",
                        "2026-10-01<br>2026-10-05")
                + "</tbody></table>";
        assertEquals(LocalDate.of(2026, 11, 5),
                AndroidSecurityBulletinChecker.parseLatestPublishedPatchLevel(html, TODAY));
    }

    @Test
    public void ignoresUnlinkedDatesAndWrongMonthInSameRow() {
        String html = "<p>2026-12-05</p>"
                + row("/docs/security/bulletin/2026/2026-11-01", "2026-12-05 2026-11-01");
        assertEquals(LocalDate.of(2026, 11, 1),
                AndroidSecurityBulletinChecker.parseLatestPublishedPatchLevel(html, TODAY));
    }

    @Test
    public void ignoresFutureBulletinDates() {
        String html = row("/docs/security/bulletin/2026/2026-12-01", "2026-12-05")
                + row("/docs/security/bulletin/2026/2026-11-01", "2026-11-05");
        assertEquals(LocalDate.of(2026, 11, 5),
                AndroidSecurityBulletinChecker.parseLatestPublishedPatchLevel(html, TODAY));
    }

    @Test
    public void doesNotInventResultWithoutVerifiedOfficialRow() {
        String html = "<div>2026-11-05</div><tr><td>Other security page"
                + " 2026-11-05</td></tr>";
        assertNull(AndroidSecurityBulletinChecker.parseLatestPublishedPatchLevel(html, TODAY));
    }

    @Test
    public void doesNotTreatTheBulletinUrlAsAnActualPatchLevel() {
        // The linked bulletin URL contains 2026-11-01, but the patch column is empty.
        String html = row("/docs/security/bulletin/2026/2026-11-01", "");
        assertNull(AndroidSecurityBulletinChecker.parseLatestPublishedPatchLevel(html, TODAY));
    }

    @Test
    public void recognizesAbsoluteBulletinLinks() {
        String html = row(
                "https://source.android.com/docs/security/bulletin/2026/2026-11-01",
                "2026-11-05");
        assertEquals(LocalDate.of(2026, 11, 5),
                AndroidSecurityBulletinChecker.parseLatestPublishedPatchLevel(html, TODAY));
    }

    @Test
    public void returnsNullForInvalidInputs() {
        assertNull(AndroidSecurityBulletinChecker.parseLatestPublishedPatchLevel(null, TODAY));
        assertNull(AndroidSecurityBulletinChecker.parseLatestPublishedPatchLevel("", TODAY));
    }
}
