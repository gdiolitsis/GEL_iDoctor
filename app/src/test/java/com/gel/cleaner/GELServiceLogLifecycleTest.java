package com.gel.cleaner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/** A configuration change must not silently erase earlier diagnostic evidence. */
public class GELServiceLogLifecycleTest {
    @Before public void resetBefore() { GELServiceLog.clear(); }
    @After public void resetAfter() { GELServiceLog.clear(); }

    @Test public void restoreSavedReportAfterProcessLocalBufferWasCleared() {
        GELServiceLog.add("LAB 1 — observed speaker response");
        GELServiceLog.add("LAB 2 — sweep completed");
        String plain = GELServiceLog.getAll();
        String html = GELServiceLog.getHtml();
        GELServiceLog.clear();
        GELServiceLog.restoreCurrentIfEmpty(plain, html);
        assertEquals(plain, GELServiceLog.getAll());
        assertEquals(html, GELServiceLog.getHtml());
        assertTrue(GELServiceLog.getAll().contains("LAB 1"));
        assertTrue(GELServiceLog.getAll().contains("LAB 2"));
    }

    @Test public void restoreNeverOverwritesOrDuplicatesActiveResults() {
        GELServiceLog.add("LAB 3 — active run");
        String current = GELServiceLog.getAll();
        GELServiceLog.restoreCurrentIfEmpty("LAB 1 — stale\n", "<b>stale</b>");
        assertEquals(current, GELServiceLog.getAll());
        assertFalse(GELServiceLog.getAll().contains("stale"));
    }

    @Test public void emptyBundleCannotManufactureAnyLaboratoryResult() {
        GELServiceLog.restoreCurrentIfEmpty(null, null);
        assertTrue(GELServiceLog.isEmpty());
        GELServiceLog.restoreCurrentIfEmpty("", "");
        assertTrue(GELServiceLog.isEmpty());
    }

    @Test public void snapshotIsBoundedEvenWithOversizedRestoredText() {
        String oversized = "X".repeat(60000);
        GELServiceLog.restoreCurrentIfEmpty(oversized, oversized);
        assertEquals(50000, GELServiceLog.getAll().length());
        assertEquals(50000, GELServiceLog.getHtml().length());
    }

    @Test public void ordinaryFinishCanStillClearTheCurrentReport() {
        GELServiceLog.add("LAB 1 — done");
        GELServiceLog.clear();
        assertTrue(GELServiceLog.isEmpty());
        assertTrue(GELServiceLog.getHtml().isEmpty());
    }
}
