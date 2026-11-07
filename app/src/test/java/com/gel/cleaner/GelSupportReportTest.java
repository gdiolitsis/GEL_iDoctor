package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class GelSupportReportTest {
    @Test public void threeLabsSurviveReportCleanupAndReachTheEmailDraft() throws Exception {
        GELServiceLog.clear();
        for (int i = 1; i <= 3; i++) {
            GELServiceLog.info("support-regression-lab-" + i);
            GELServiceLog.labFinished("support-regression-lab-" + i);
            GELServiceLog.clear(); // lab exit / PDF export cleanup
        }
        assertTrue(GELServiceLog.isEmpty()); // report lifecycle is unchanged
        Context context = RuntimeEnvironment.getApplication();
        String report = GelSupportReport.build(context, "My description");
        for (int i = 1; i <= 3; i++) assertTrue(report.contains("support-regression-lab-" + i));
        assertTrue(report.contains("My description"));
        assertFalse(report.contains("No diagnostic logs"));
        PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        assertTrue(report.contains("Version: " + info.versionName + " (" + info.getLongVersionCode() + ")"));
    }

    @Test public void supportHistoryIsBoundedAndKeepsNewestDiagnostics() {
        GELServiceLog.info("old-support-marker");
        String line = new String(new char[1000]).replace('\0', 'x');
        for (int i = 0; i < 60; i++) GELServiceLog.info(line);
        GELServiceLog.error("newest-support-marker");
        String logs = GELServiceLog.getSupportLog();
        assertTrue(logs.length() <= 50000);
        assertFalse(logs.contains("old-support-marker"));
        assertTrue(logs.contains("newest-support-marker"));
    }
}
