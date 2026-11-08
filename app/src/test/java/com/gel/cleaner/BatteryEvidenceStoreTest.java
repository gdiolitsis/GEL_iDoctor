package com.gel.cleaner;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/** Persisted Lab 14A facts must survive activity restart and fail closed on invalid runs. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class BatteryEvidenceStoreTest {
    private Context context;

    @Before public void setUp() {
        context = RuntimeEnvironment.getApplication();
        BatteryEvidenceStore.clearLab14(context);
    }

    @Test public void noRecordMeansUnknownNotHealthy() {
        assertNull(BatteryEvidenceStore.readLatestLab14(context));
    }

    @Test public void completedRunCarriesVersionedEvidenceWithoutInventedSoH() {
        assertTrue(BatteryEvidenceStore.recordLab14(context, true, true, false));
        BatteryEvidenceStore.Record record = BatteryEvidenceStore.readLatestLab14(context);
        assertNotNull(record);
        assertTrue(record.completed);
        assertTrue(record.drainValid);
        assertTrue(record.isFresh(60_000L));
        assertTrue(Double.isNaN(record.validatedSoHPercent()));
    }

    @Test public void incompleteRunCannotPassResultGate() {
        assertTrue(BatteryEvidenceStore.recordLab14(context, false, true, false));
        BatteryEvidenceStore.Record record = BatteryEvidenceStore.readLatestLab14(context);
        assertNotNull(record);
        assertFalse(record.isCompletedValidRun(60_000L));
        assertTrue(Double.isNaN(record.validatedSoHPercent()));
    }

    @Test public void explicitClearPreventsStaleGreenStatus() {
        assertTrue(BatteryEvidenceStore.recordLab14(context, true, true, false));
        BatteryEvidenceStore.clearLab14(context);
        assertNull(BatteryEvidenceStore.readLatestLab14(context));
    }
}
