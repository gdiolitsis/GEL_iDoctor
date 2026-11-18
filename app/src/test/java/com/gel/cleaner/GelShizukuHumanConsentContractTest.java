package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Human-facing Shizuku permission text must remain readable and accurate. */
public final class GelShizukuHumanConsentContractTest {
    private static String source() throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/GelShizukuUi.java");
        if (!Files.isRegularFile(path)) path = Paths.get("src/main/java/com/gel/cleaner/GelShizukuUi.java");
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Test public void clearQuestionAndOptionalNoPath() throws Exception {
        String s = source();
        assertTrue(s.contains("Με εξουσιοδοτείς να ζητήσω από το Android περισσότερες πληροφορίες"));
        assertTrue(s.contains("χωρίς τη συγκατάθεσή σου;"));
        assertTrue(s.contains("ΟΧΙ"));
        assertTrue(s.contains("ΝΑΙ, ΕΓΚΡΙΝΩ"));
        assertTrue(s.contains("όλα τα εργαστήρια συνεχίζουν κανονικά"));
        assertTrue(s.contains("cancel.setOnClickListener(v -> dialog.dismiss())"));
        assertTrue(s.contains("session.requestFromUser()"));
        assertTrue(s.contains("if (entryOfferShown) return;"));
    }

    @Test public void meaningfulPrivacyDisclosureAndReadOnlyClaimStayVisible() throws Exception {
        String s = source();
        assertTrue(s.contains("Ονόματα εφαρμογών και καταγεγραμμένοι χρόνοι πρόσβασης"));
        assertTrue(s.contains("σε κάμερα, μικρόφωνο ή τοποθεσία"));
        assertTrue(s.contains("Δεν αλλάζω ρυθμίσεις ή αρχεία"));
        assertTrue(s.contains("θα χρειαστεί και έγκριση από την εφαρμογή Shizuku")
                || s.contains("Θα χρειαστεί και έγκριση από την εφαρμογή Shizuku"));
        assertTrue(s.contains("The Shizuku app will also ask for permission."));
        assertTrue(s.contains("recorded times of camera, microphone or location access"));
        assertTrue(s.contains("cancel.setBackgroundColor(0xFF444444)"));
    }
}
