package com.gel.cleaner;

import android.content.Context;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class GelReportCreditTest {
    private Context context;
    private GelBillingManager billing;
    @Before public void setup() {
        context = RuntimeEnvironment.getApplication();
        context.getSharedPreferences("GEL_CUSTOM_REPORT_ENTITLEMENT", 0).edit().clear()
                .putInt("single_report_credits", 1).commit();
        billing = new GelBillingManager(context, new GelProBillingListener());
    }
    @Test public void successfulReportIsChargedOnceIncludingAfterRecreation() {
        assertTrue(billing.consumeSingleReportCreditForReport("report-a"));
        assertEquals(0, billing.getSingleReportCredits());
        GelBillingManager restored = new GelBillingManager(context, new GelProBillingListener());
        assertTrue(restored.isReportPreviouslyExported("report-a"));
        assertTrue(restored.consumeSingleReportCreditForReport("report-a"));
        assertFalse(restored.consumeSingleReportCreditForReport("report-b"));
        assertEquals(0, restored.getSingleReportCredits());
    }
    @Test public void cancelledOrFailedExportDoesNotDebitAndInvalidIdentityIsRejected() {
        assertFalse(billing.isReportPreviouslyExported("report-a"));
        assertFalse(billing.consumeSingleReportCreditForReport(""));
        assertFalse(billing.consumeSingleReportCreditForReport(null));
        assertEquals(1, billing.getSingleReportCredits());
    }
}
