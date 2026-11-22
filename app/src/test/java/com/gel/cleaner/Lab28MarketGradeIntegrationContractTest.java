package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** Lab 28 informs buyers; choosing or recommending any listing is prohibited. */
public final class Lab28MarketGradeIntegrationContractTest {
    static String readActivity() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void gradeIsReadOnlyContextNotAnEbayFilter() throws Exception {
        String s = readActivity();
        int a = s.indexOf("private void runLab29DeviceAuthenticity()");
        int b = s.indexOf("private boolean lab29RequireLab25Evidence()", a);
        assertTrue(a >= 0 && b > a);
        String lab = s.substring(a,b);
        assertTrue(lab.contains("lab28RequireCertification()"));
        assertTrue(lab.contains("lab27_cert_grade"));
        assertTrue(lab.contains("lab27_cert_functional_findings"));
        assertTrue(lab.contains("lab27_cert_camera_failure"));
        assertTrue(lab.contains("showLab28MarketResearchDialog(gr)"));
        assertFalse(lab.contains("conditionForGrade("));
        assertFalse(lab.contains("Μην πληρώνεις"));
        assertFalse(lab.contains("Το 50%"));
    }

    @Test public void buyerSeesEbayPhotosAndPricesAndRetainsFullChoice() throws Exception {
        String s = readActivity();
        int a = s.indexOf("private void showLab28MarketResearchDialog(boolean gr)");
        int b = s.indexOf("private Button lab29DialogButton(", a);
        assertTrue(a >= 0 && b > a);
        String market = s.substring(a,b);
        assertTrue(market.contains("πραγματικές φωτογραφίες των αγγελιών"));
        assertTrue(market.contains("να έχει ραγισμένη οθόνη"));
        assertTrue(market.contains("Εσύ αποφασίζεις"));
        assertTrue(market.contains("χωρίς φίλτρα GEL"));
        assertTrue(market.contains("Δεν προτείνουμε αγορά"));
        assertTrue(market.contains("Lab28MarketResearch.browseSearchUrl(model, uk)"));
        assertTrue(market.contains("new Intent(Intent.ACTION_VIEW"));
        assertFalse(market.contains("imei"));
        assertFalse(market.contains("getDeviceId("));
    }
}
