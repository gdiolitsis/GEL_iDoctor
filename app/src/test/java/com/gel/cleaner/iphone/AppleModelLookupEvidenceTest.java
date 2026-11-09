package com.gel.cleaner.iphone;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppleModelLookupEvidenceTest {
    @Test public void base16IsExact() {
        assertEquals("iPhone 16", AppleSpecs.get("iPhone 16").model);
    }
    @Test public void pro16MaxIsExact() {
        assertEquals("iPhone 16 Pro Max", AppleSpecs.get("iPhone 16 Pro Max").model);
    }
    @Test public void plusVariantIsNotBase() {
        assertEquals("iPhone 16 Plus", AppleSpecs.get("iPhone 16 Plus").model);
    }
    @Test public void 16eDoesNotInherit16Hardware() {
        assertEquals("unknown", AppleSpecs.get("iPhone 16e").type);
    }
    @Test public void future18ModelDoesNotInheritOlderSpecs() {
        assertEquals("unknown", AppleSpecs.get("iPhone 18 Pro").type);
    }
    @Test public void future17ModelDoesNotInheritOlderSpecs() {
        assertEquals("unknown", AppleSpecs.get("iPhone 17 Pro Max").type);
    }
    @Test public void mini7DoesNotInheritMini6Hardware() {
        assertEquals("unknown", AppleSpecs.get("iPad mini 7").type);
    }
    @Test public void mini6Known() {
        assertEquals("iPad mini 6", AppleSpecs.get("iPad mini 6").model);
    }
    @Test public void seriesSuffixNormalizesSafely() {
        assertEquals("iPhone 15 Pro", AppleSpecs.get("iPhone 15 Pro Series").model);
    }
    @Test public void noSubstringIdentityConfusion() {
        assertEquals("unknown", AppleSpecs.get("X iPhone 16 Pro Max repair case").type);
    }
    @Test public void noNullFalseSpec() {
        assertEquals("unknown", AppleSpecs.get(null).type);
    }
    @Test public void noEmptyFalseSpec() {
        assertEquals("unknown", AppleSpecs.get("  ").type);
    }
    @Test public void localeAgnosticInTurkish() {
        java.util.Locale prev=java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(new java.util.Locale("tr","TR"));
            assertEquals("iPhone 15 Pro", AppleSpecs.get("IPHONE 15 PRO").model);
        } finally {
            java.util.Locale.setDefault(prev);
        }
    }
}
