package com.gel.cleaner;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab28MarketResearchTest {
    final Lab28MarketResearch.Model model =
            Lab28MarketResearch.identify("Xiaomi", "23076RN8DY",
                    "Xiaomi Redmi 12 5G (23076RN8DY)").withCapacity(128,4);

    private Lab28MarketResearch.Listing offer(int id, int euro,
            Lab28MarketResearch.SaleCondition condition) {
        return new Lab28MarketResearch.Listing(
                "Xiaomi Redmi 12 5G 128 GB 4 GB RAM Used",
                "https://www.ebay.de/itm/" + id, euro * 100L, 500, condition);
    }

    @Test public void searchUsesActualModelAndNeverLeaksGradeOrImei() {
        String a=Lab28MarketResearch.browseSearchUrl(model,false);
        String b=Lab28MarketResearch.browseSearchUrl(model,true);
        assertTrue(a.startsWith("https://www.ebay.de/sch/i.html?_nkw="));
        assertTrue(a.contains("Redmi+12+5G"));
        assertTrue(a.contains("128GB"));
        assertTrue(b.contains("defekt+camera"));
        assertFalse(a.toLowerCase().contains("imei"));
        assertFalse(a.contains("A%2B"));
    }

    @Test public void modelGuardExcludesConfusingSkusAndCapacities() {
        assertTrue(Lab28MarketResearch.titleMatches(model,"Redmi 12 5G 128GB 4GB RAM"));
        assertFalse(Lab28MarketResearch.titleMatches(model,"Redmi Note 12 5G 128GB"));
        assertFalse(Lab28MarketResearch.titleMatches(model,"Redmi 12 4G 128GB"));
        assertFalse(Lab28MarketResearch.titleMatches(model,"Redmi 12 5G 256GB"));
        assertFalse(Lab28MarketResearch.titleMatches(model,"Redmi 12 5G 128GB / 256GB"));
        assertFalse(Lab28MarketResearch.titleMatches(model,"Redmi 12 5G 128GB 8GB RAM"));
        assertFalse(Lab28MarketResearch.titleMatches(model,"Redmi 12 5G 23076RN4BI 128GB"));
        assertFalse(Lab28MarketResearch.titleMatches(model.withCapacity(0,0),"Redmi 12 5G 128GB"));
    }

    @Test public void onlyGenuineHttpsEbayItemUrlsPass() {
        assertTrue(Lab28MarketResearch.isAllowedListingUrl("https://www.ebay.de/itm/123"));
        assertTrue(Lab28MarketResearch.isAllowedListingUrl("https://www.ebay.co.uk/itm/123"));
        assertFalse(Lab28MarketResearch.isAllowedListingUrl("http://www.ebay.de/itm/123"));
        assertFalse(Lab28MarketResearch.isAllowedListingUrl("https://www.ebay.de.attacker.com/itm/123"));
        assertFalse(Lab28MarketResearch.isAllowedListingUrl("https://www.ebay.de/sch/i.html"));
        assertFalse(Lab28MarketResearch.isAllowedListingUrl("https://user@www.ebay.de/itm/123"));
    }

    @Test public void threeDistinctPricesAndShippingProduceAWorkingMedian() {
        Lab28MarketResearch.SaleCondition working = Lab28MarketResearch.SaleCondition.CLAIMED_WORKING;
        Lab28MarketResearch.Listing a=offer(1,60,working);
        Lab28MarketResearch.Listing b=offer(2,80,working);
        Lab28MarketResearch.Listing c=offer(3,100,working);
        Lab28MarketResearch.Summary s = Lab28MarketResearch.summarize(model,Arrays.asList(a,b,c,a),working);
        assertEquals(3,s.count);
        assertEquals(6500,s.minCents);
        assertEquals(8500,s.medianCents);
        assertEquals(10500,s.maxCents);
        assertTrue(s.usable());
        assertFalse(Lab28MarketResearch.summarize(model,Arrays.asList(a,b),working).usable());
        assertEquals(0,Lab28MarketResearch.summarize(model,Arrays.asList(a,b),Lab28MarketResearch.SaleCondition.CLAIMED_DEFECTIVE).count);
    }

    @Test public void noFakeSameGradeMarketMatch() {
        Lab28MarketResearch.Summary s=Lab28MarketResearch.summarize(model,
                Arrays.asList(offer(1,60,Lab28MarketResearch.SaleCondition.CLAIMED_WORKING),
                              offer(2,80,Lab28MarketResearch.SaleCondition.CLAIMED_WORKING),
                              offer(3,100,Lab28MarketResearch.SaleCondition.CLAIMED_WORKING)),
                Lab28MarketResearch.SaleCondition.CLAIMED_WORKING);
        String desc=Lab28MarketResearch.describeGradeComparison("D",s,null,true);
        assertTrue(desc.contains("Βαθμός αυτής της συσκευής: Δ"));
        assertTrue(desc.contains("ΚΑΜΙΑ αγγελία δεν θεωρείται ίδιου βαθμού GEL"));
        assertTrue(desc.contains("85,00"));
    }

    @Test public void priceParsingAcceptsBothLocaleSeparatorsButRejectsInventedData() {
        assertEquals(7050,Lab28MarketResearch.parseEuroCents("70,50"));
        assertEquals(7050,Lab28MarketResearch.parseEuroCents("70.50 €"));
        assertEquals(-1,Lab28MarketResearch.parseEuroCents("GBP 70"));
        assertEquals(-1,Lab28MarketResearch.parseEuroCents(""));
        assertEquals(-1,Lab28MarketResearch.parseEuroCents("0"));
        assertEquals(128,Lab28MarketResearch.estimateNominalStorage(110L*1073741824L));
        assertEquals(0,Lab28MarketResearch.estimateNominalStorage(77L*1073741824L));
    }
}
