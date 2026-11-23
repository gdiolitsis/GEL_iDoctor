package com.gel.cleaner;

import org.junit.Test;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public final class AppleSymptomSearchTest {
    private Path locate(String relative) {
        Path here = Paths.get(relative);
        return Files.isRegularFile(here) ? here : Paths.get("app").resolve(relative);
    }

    private static final class Sources {
        List<AppleOfficialServiceGuidance.Guidance> guides;
        List<AppleIPhoneManualChapters.Chapter> chapters;
        List<AppleSymptomSearch.Terms> terms;
    }

    private Sources sourceData() throws Exception {
        Sources data = new Sources();
        try (FileInputStream in = new FileInputStream(locate(
                "src/main/assets/iphone_apple_official_guidance_v1.tsv").toFile())) {
            data.guides = AppleOfficialServiceGuidance.parse(in);
        }
        try (FileInputStream in = new FileInputStream(locate(
                "src/main/assets/iphone_apple_manual_chapters_v1.tsv").toFile())) {
            data.chapters = AppleIPhoneManualChapters.parse(in, data.guides);
        }
        try (FileInputStream in = new FileInputStream(locate(
                "src/main/assets/iphone_apple_symptom_search_terms_v1.tsv").toFile())) {
            data.terms = AppleSymptomSearch.readTerms(in);
        }
        return data;
    }

    private List<AppleSymptomSearch.Hit> search(Sources sources, String model,
            String question) {
        AppleOfficialServiceGuidance.Guidance manual =
                AppleTroubleshootingLibrary.manualForSelection(
                        sources.guides, "iphone", model);
        assertNotNull("Exact Apple manual missing for " + model, manual);
        return AppleSymptomSearch.search(question, manual,
                sources.chapters, sources.guides, sources.terms);
    }

    private AppleSymptomSearch.Hit find(List<AppleSymptomSearch.Hit> hits,
            String topic) {
        for (AppleSymptomSearch.Hit hit : hits) {
            if (topic.equals(hit.topic)) return hit;
        }
        return null;
    }

    @Test public void spontaneousRestartMapsToSelectedIphoneChapter() throws Exception {
        Sources s = sourceData();
        AppleSymptomSearch.Hit hit = find(search(
                s, "iPhone 17 Pro", "Το κινητό σβήνει μόνο του χωρίς λόγο"), "RESTART");
        assertNotNull("Expected indexed Apple restart chapter", hit);
        assertTrue(hit.modelIndexed);
        assertTrue(AppleTroubleshootingLibrary.isOfficialUrl(hit.url));
    }

    @Test public void slowChargingMapsToModelChapterNotAnotherIphone() throws Exception {
        Sources s = sourceData();
        AppleSymptomSearch.Hit hit = find(search(
                s, "iPhone 17 Pro", "Δεν φορτίζει, η φόρτιση είναι πολύ αργή"),
                "WIRED_CHARGING");
        assertNotNull("Expected model wired charging chapter", hit);
        assertTrue(hit.modelIndexed);
        assertEquals("https://support.apple.com/en-us/101969", hit.url);
    }

    @Test public void overheatingAndWifiAreMarkedGenericWhenNotModelSpecific()
            throws Exception {
        Sources s = sourceData();
        AppleSymptomSearch.Hit hot = find(search(s, "iPhone 17 Pro",
                "Το κινητό ζεσταίνεται και έχει υπερθέρμανση"), "THERMAL");
        assertNotNull(hot);
        assertFalse(hot.modelIndexed);
        AppleSymptomSearch.Hit wifi = find(search(s, "iPhone 17 Pro",
                "Το Wi-Fi αποσυνδέεται από το ίντερνετ"), "WIFI");
        assertNotNull(wifi);
        assertFalse(wifi.modelIndexed);
    }

    @Test public void specificMoistureSymptomFindsActualAppleArticle() throws Exception {
        Sources s = sourceData();
        AppleSymptomSearch.Hit moisture = find(search(
                s, "iPhone 17 Pro", "Βρήκα υγρασία και νερό στη θύρα"),
                "LIQUID_CONNECTOR");
        assertNotNull(moisture);
        assertFalse(moisture.modelIndexed);
        assertEquals("https://support.apple.com/en-us/102643", moisture.url);
    }

    @Test public void noRecognizableSymptomNeverInventsSuggestion() throws Exception {
        Sources s = sourceData();
        assertTrue(search(s, "iPhone 17 Pro", "άσχετη άγνωστη περιγραφή")
                .isEmpty());
        assertTrue(search(s, "iPhone 17 Pro", "").isEmpty());
        assertTrue(search(s, "iPhone 17 Pro", "το κινητό έχει πρόβλημα").isEmpty());
        assertTrue(search(s, "iPhone 17 Pro", "ο και στο μου").isEmpty());
    }

    @Test public void allResultsComeFromChosenModelOrOfficialGenericApple()
            throws Exception {
        Sources s = sourceData();
        List<AppleSymptomSearch.Hit> results = search(s, "iPhone 17 Pro",
                "Η οθόνη δεν αποκρίνεται στην αφή");
        assertFalse(results.isEmpty());
        assertTrue(results.size() <= 6);
        AppleOfficialServiceGuidance.Guidance manual =
                AppleTroubleshootingLibrary.manualForSelection(
                        s.guides, "iphone", "iPhone 17 Pro");
        for (AppleSymptomSearch.Hit h : results) {
            assertTrue(AppleTroubleshootingLibrary.isOfficialUrl(h.url));
            if (h.modelIndexed) {
                boolean present = false;
                for (AppleIPhoneManualChapters.Chapter c : s.chapters) {
                    if (manual.id.equals(c.manualId) && c.sourceUrl.equals(h.url)
                            && c.phase == AppleIPhoneManualChapters.Phase.TROUBLESHOOT)
                        present = true;
                }
                assertTrue("Result belongs to another model: " + h.url, present);
            }
        }
    }

    @Test public void noFixedIssueChoicesAndGoldMainButton() throws Exception {
        String main = new String(Files.readAllBytes(locate(
                "src/main/java/com/gel/cleaner/MainActivity.java")), StandardCharsets.UTF_8);
        String xml = new String(Files.readAllBytes(locate(
                "src/main/res/layout/activity_main.xml")), StandardCharsets.UTF_8);
        String popup = main.substring(
                main.indexOf("private void showAppleTroubleshootingSymptoms("),
                main.indexOf("private LinearLayout newAppleReferenceBox("));
        assertTrue(popup.contains("new EditText(this)"));
        assertTrue(popup.contains("AppleSymptomSearch.search(query"));
        assertTrue(popup.contains("Περιέγραψε με δικά σου λόγια"));
        assertFalse("No fixed topic-button inventory", popup.contains(
                "for (AppleTroubleshootingLibrary.TopicLink"));
        assertTrue(popup.contains("newAppleReferenceBox("));
        assertTrue(popup.contains("dialog::dismiss"));
        assertTrue(popup.contains("0xFFFFD700"));
        assertTrue(main.contains("((TextView) library).setTextColor(0xFFFFD700)"));
        int start = xml.indexOf("@+id/btnAppleTroubleshooting");
        int end = xml.indexOf("/>", start);
        assertTrue(start > 0 && end > start);
        assertTrue(xml.substring(start, end).contains(
                "android:textColor=\"#FFD700\""));
    }
}
