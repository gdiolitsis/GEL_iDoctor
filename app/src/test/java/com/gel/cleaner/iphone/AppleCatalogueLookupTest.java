package com.gel.cleaner.iphone;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppleCatalogueLookupTest {
    private static void known(String name) {
        assertNotEquals("unknown", AppleSpecs.get(name).type);
    }
    private static void unknown(String name) {
        assertEquals("unknown", AppleSpecs.get(name).type);
    }
    @Test public void recognizesKnown16() { known("iPhone 16"); }
    @Test public void recognizesKnown16Plus() { known("iPhone 16 Plus"); }
    @Test public void recognizesKnown16Pro() { known("iPhone 16 Pro"); }
    @Test public void recognizesKnown16ProMax() { known("iPhone 16 Pro Max"); }
    @Test public void recognizes11() { known("iPhone 11"); }
    @Test public void recognizes12Mini() { known("iPhone 12 mini"); }
    @Test public void recognizes13ProMax() { known("iPhone 13 Pro Max"); }
    @Test public void rejects16eFalselyResolvedAs16() { unknown("iPhone 16e"); }
    @Test public void rejects17WithoutVerifiedSpecs() { unknown("iPhone 17"); }
    @Test public void rejects18ProWithoutVerifiedSpecs() { unknown("iPhone 18 Pro"); }
    @Test public void rejectsConcatenatedSKU() { unknown("iPhone 16 Pro Extra"); }
    @Test public void rejectsUnknownPadMiniGeneration() { unknown("iPad mini 7"); }
}
