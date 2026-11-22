package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab28MarketResearchTest {
    private final Lab28MarketResearch.Model redmi =
        Lab28MarketResearch.identify("Xiaomi", "23076RN8DY",
                "Xiaomi Redmi 12 5G (23076RN8DY)");

    @Test public void buyerCanBrowseFullListingGalleryOnTwoMarketplaces() {
        String eu = Lab28MarketResearch.browseSearchUrl(redmi, false);
        String uk = Lab28MarketResearch.browseSearchUrl(redmi, true);
        assertTrue(eu.startsWith("https://www.ebay.de/sch/i.html?_nkw="));
        assertTrue(uk.startsWith("https://www.ebay.co.uk/sch/i.html?_nkw="));
        assertTrue(eu.contains("Xiaomi+Redmi+12+5G"));
        assertTrue(uk.contains("Xiaomi+Redmi+12+5G"));
    }

    @Test public void noGELRankingFaultRamOrCosmeticFiltersLeakToMarketplace() {
        String search = Lab28MarketResearch.browseSearchUrl(redmi, false);
        assertFalse(search.contains("23076RN8DY"));
        assertFalse(search.contains("128GB"));
        assertFalse(search.contains("4GB"));
        assertFalse(search.contains("defekt"));
        assertFalse(search.contains("camera"));
        assertFalse(search.contains("grade"));
        assertFalse(search.contains("imei"));
        assertFalse(search.contains("LH_"));
        assertFalse(search.contains("_sop="));
        assertFalse(search.contains("cracked"));
    }

    @Test public void unrecognizedModelKeepsItsHonestPublicName() {
        Lab28MarketResearch.Model other =
                Lab28MarketResearch.identify("Motorola", "XT12345", "");
        assertEquals("Motorola XT12345", other.searchQuery());
        assertEquals("Xiaomi Redmi 12 5G", redmi.searchQuery());
    }

    @Test(expected=IllegalArgumentException.class)
    public void emptyModelCannotProduceMeaninglessSearch() {
        Lab28MarketResearch.browseSearchUrl(
                new Lab28MarketResearch.Model("", ""), false);
    }
}
