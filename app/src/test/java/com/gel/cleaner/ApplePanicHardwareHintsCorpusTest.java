package com.gel.cleaner;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * PUBLIC-SOURCE-INSPIRED, MINIMAL RECONSTRUCTIONS. NO ORIGINAL CUSTOMER DATA.
 *
 * The corpus has strict expected results and negative controls. This is
 * regression coverage, not clinical/repair accuracy or OEM validation.
 */
public final class ApplePanicHardwareHintsCorpusTest {
    private static final String CORPUS = "iphone_panic_corpus.tsv";

    @Test public void sourceBackedCorpusHasAtLeastFortyIndependentCases() throws Exception {
        int cases = 0, negatives = 0, experiments = 0, conflicting = 0;
        for (String[] row : records()) {
            cases++;
            assertTrue("missing source URL for " + row[0], row[5].startsWith("https://"));
            assertFalse("Missing tier for " + row[0], row[6].trim().isEmpty());
            if (row[3].equals("-")) negatives++;
            if (row[6].equals("CONTROLLED_DISCONNECTION")) experiments++;
            if (row[6].equals("CONFLICTING_REFERENCES")) conflicting++;
        }
        assertTrue("insufficient corpus size: " + cases, cases >= 40);
        assertTrue("needs explicit negative controls", negatives >= 5);
        assertTrue("needs controlled experimental references", experiments >= 5);
        assertTrue("needs conflicting-source safeguards", conflicting >= 2);
    }

    @Test public void everyPanicFixtureYieldsOnlyDocumentedCodes() throws Exception {
        for (String[] row : records()) {
            String id = row[0], model = row[1], input = row[2].replace("\\n", "\n");
            ApplePanicHardwareHints.Assessment result =
                    ApplePanicHardwareHints.examineOne(input);
            List<String> observed = new ArrayList<>();
            for (ApplePanicHardwareHints.Finding hint : result.findings) {
                observed.add(hint.code);
                assertFalse("Part claimed as confirmed in " + id,
                        hint.areaGreek.contains("επιβεβαιωμένη βλάβη") &&
                        !hint.areaGreek.contains("Όχι επιβεβαιωμένη"));
                assertFalse("Missing technician caveat " + id, hint.action(true).isEmpty());
            }
            String expected = row[3];
            List<String> wanted = expected.equals("-")
                    ? new ArrayList<>() : Arrays.asList(expected.split(","));
            assertEquals(id + " expected " + wanted + " but produced " + observed,
                    wanted, observed);
            if (!wanted.isEmpty()) {
                assertEquals("model must stay scoped per test case " + id,
                        model.toLowerCase(Locale.ROOT), result.modelIdentifier);
            }
            if (!"-".equals(row[4])) {
                assertFalse("Missing expected area for " + id, result.findings.isEmpty());
                assertTrue(id + " expected area fragment " + row[4] + " but got " +
                                result.findings.get(0).areaGreek,
                        result.findings.get(0).areaGreek.contains(row[4]));
            }
        }
    }

    @Test public void sameCodeMustNeverMapAcrossDifferentPhoneModels() {
        String fourteenPlus = panic("iPhone14,8", "0x400000");
        String fourteenPro = panic("iPhone15,2", "0x400000");
        String unmapped = panic("iPhone19,9", "0x400000");
        ApplePanicHardwareHints.Finding a = one(fourteenPlus);
        ApplePanicHardwareHints.Finding b = one(fourteenPro);
        ApplePanicHardwareHints.Finding c = one(unmapped);
        assertTrue(a.modelScoped);
        assertFalse(b.modelScoped);
        assertFalse(c.modelScoped);
        assertTrue(a.areaGreek.contains("πηνίο"));
        assertTrue(b.areaGreek.contains("δεν υπάρχει"));
    }

    @Test public void unknownOrDisputedSmaCodeDoesNotTriggerRandomPartSwap() {
        ApplePanicHardwareHints.Finding unknown = one(panic("iPhone14,8", "0x500000"));
        assertFalse(unknown.modelScoped);
        assertTrue(unknown.areaGreek.contains("δεν υπάρχει"));
        assertTrue(unknown.action(true).contains("Μην αγοράσεις ανταλλακτικό"));
    }

    @Test public void separatePanicFilesCannotCombineIntoFalsePartDiagnosis() {
        String first = "===== ZIP FILE: panic-a.ips =====\n"
                + "{\"product\":\"iPhone12,1\",\"panicString\":\"panic(cpu 0): "
                + "watchdog timeout with Missing sensor(s): mic2\"}\n";
        String second = "===== ZIP FILE: panic-b.ips =====\n"
                + "{\"product\":\"iPhone14,8\",\"panicString\":\"panic(cpu 1): "
                + "SMC PANIC - ASSERTION FAILED SMC BSC failure "
                + "S.sensor array 0 - 4 is 0x0, 0x400000, 0x0, 0x0, 0x0\"}\n";
        assertEquals("MIC2", one(first).code);
        assertEquals("SMC 0x400000", one(second).code);
        assertFalse(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                first + second, Arrays.asList("Missing sensor(s): mic2", "SMC BSC failure")));
    }

    @Test public void explicitMissingSensorIsRequiredNotRawHardwareMetadata() {
        String inventory = "{\"product\":\"iPhone12,1\",\"panicString\":"
                + "\"panic(cpu 1): kernel crash; inventory mic2; system sensor list\"}";
        assertTrue(ApplePanicHardwareHints.examineOne(inventory).findings.isEmpty());
        String known = "{\"product\":\"iPhone12,1\",\"panicString\":"
                + "\"panic(cpu 1): watchdog; Missing sensor(s): mic2\"}";
        assertEquals("MIC2", one(known).code);
    }

    private static String panic(String model, String code) {
        return "{\"product\":\"" + model + "\",\"panicString\":\"panic(cpu 1): "
                + "SMC PANIC - ASSERTION FAILED; SMC BSC failure; "
                + "S.sensor array 0 - 4 is 0x0, " + code + ", 0x0, 0x0, 0x0\"}";
    }

    private static ApplePanicHardwareHints.Finding one(String input) {
        ApplePanicHardwareHints.Assessment result =
                ApplePanicHardwareHints.examineOne(input);
        assertEquals("Expected a single supported finding", 1, result.findings.size());
        return result.findings.get(0);
    }

    private static List<String[]> records() throws Exception {
        InputStream stream =
                ApplePanicHardwareHintsCorpusTest.class.getClassLoader()
                        .getResourceAsStream(CORPUS);
        assertNotNull("QA corpus missing from test resources", stream);
        List<String[]> cases = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] row = line.split("\t", -1);
                assertEquals("Invalid fixture row: " + line, 7, row.length);
                cases.add(row);
            }
        }
        return cases;
    }
}
