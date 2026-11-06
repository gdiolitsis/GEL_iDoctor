package com.gel.cleaner;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class GelReportCreditTest {
    @Test public void signedOutUserCannotUseLegacyOrOtherAccountCredits() {
        Context context = RuntimeEnvironment.getApplication();
        FirebaseApp.initializeApp(context);
        FirebaseAuth.getInstance().signOut();
        context.getSharedPreferences("GEL_CUSTOM_REPORT_ENTITLEMENT", 0).edit()
                .putInt("single_report_credits", 50).putBoolean("active",true).commit();
        context.getSharedPreferences("GEL_REPORT_SERVER_other-user", 0).edit()
                .putInt("credits", 10).putBoolean("unlimited",true).commit();
        assertEquals(0, GelReportStore.credits(context));
        assertFalse(GelReportStore.unlimited(context));
        GelBillingManager billing = new GelBillingManager(context, new GelProBillingListener());
        assertEquals(0,billing.getSingleReportCredits());
        assertFalse(billing.isCustomReportsActive());
    }
}
