package com.gel.cleaner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Buyer market comparison, using REAL user-supplied eBay asking listings.
 * No scraping, fake current prices, external credentials, IMEI transmission,
 * or inference that seller descriptions are verified GEL grades.
 */
public final class Lab28MarketResearch {
    private Lab28MarketResearch() {}

    public enum SaleCondition { CLAIMED_WORKING, CLAIMED_DEFECTIVE, UNKNOWN }

    public static final class Model {
        public final String name, code;
        public final int storageGb, ramGb;
        public Model(String name, String code, int storageGb, int ramGb) {
            this.name = safe(name);
            this.code = safe(code);
            this.storageGb = storageGb;
            this.ramGb = ramGb;
        }
        public Model withCapacity(int storage, int ram) {
            return new Model(name, code, storage, ram);
        }
        public String query() {
            String q = name.isEmpty() ? code : name;
            if (storageGb > 0) q += " " + storageGb + "GB";
            if (ramGb > 0) q += " " + ramGb + "GB RAM";
            return q.trim();
        }
    }

    public static Model identify(String manufacturer, String androidCode, String officialName) {
        String code = safe(androidCode);
        if ("23076RN8DY".equalsIgnoreCase(code))
            return new Model("Xiaomi Redmi 12 5G", code, 0, 0);
        String market = safe(officialName).replaceAll("\\s*\\([^)]*\\)\\s*$", "").trim();
        if (market.isEmpty() || market.equalsIgnoreCase(code))
            market = (safe(manufacturer) + " " + code).trim();
        return new Model(market, code, 0, 0);
    }

    /** Android /data partition size is only a HINT, not proven nominal flash capacity. */
    public static int estimateNominalStorage(long partitionBytes) {
        if (partitionBytes <= 0) return 0;
        double gib = partitionBytes / 1073741824d;
        if (gib >= 52 && gib <= 64) return 64;
        if (gib >= 96 && gib <= 128) return 128;
        if (gib >= 205 && gib <= 256) return 256;
        if (gib >= 425 && gib <= 512) return 512;
        return 0;
    }

    public static int estimateNominalRam(long totalBytes) {
        if (totalBytes <= 0) return 0;
        double gib = totalBytes / 1073741824d;
        for (int nominal : new int[]{2, 3, 4, 6, 8, 12, 16}) {
            if (gib >= nominal * 0.80 && gib <= nominal * 1.03) return nominal;
        }
        return 0;
    }

    public static String browseSearchUrl(Model model, boolean nonWorking) {
        String query = model.query() + (nonWorking ? " defekt camera" : "");
        // Search does not contain GEL grade, IMEI, serial or personal identifiers.
        try {
            return "https://www.ebay.de/sch/i.html?_nkw="
                    + URLEncoder.encode(query, StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException never) {
            throw new IllegalStateException(never);
        }
    }

    public static final class Listing {
        public final String title, url;
        public final long askingCents, shippingCents;
        public final SaleCondition sellerCondition;
        public Listing(String title, String url, long askingCents, long shippingCents,
                       SaleCondition sellerCondition) {
            this.title = safe(title);
            this.url = safe(url);
            this.askingCents = askingCents;
            this.shippingCents = shippingCents;
            this.sellerCondition = sellerCondition == null
                    ? SaleCondition.UNKNOWN : sellerCondition;
        }
        public long deliveredCents() { return askingCents + shippingCents; }
    }

    public static boolean isAllowedListingUrl(String input) {
        if (input == null) return false;
        try {
            URI u = URI.create(input.trim());
            if (!"https".equalsIgnoreCase(u.getScheme())
                    || u.getRawUserInfo() != null || u.getPort() != -1) return false;
            String host = u.getHost() == null ? "" : u.getHost().toLowerCase(Locale.ROOT);
            if (!host.matches("(www\\.)?ebay\\.(de|com|co\\.uk|fr|it|es|nl|be|at)"))
                return false;
            String path = u.getPath();
            return path != null && path.matches("/itm/.*");
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** Seller title is a weak claim; verify model, capacity and listing URL only. */
    public static boolean titleMatches(Model model, String listingTitle) {
        if (model == null || model.storageGb <= 0 || listingTitle == null) return false;
        String t = listingTitle.toLowerCase(Locale.ROOT);
        if ("23076rn8dy".equalsIgnoreCase(model.code)) {
            if (!t.matches("(?s).*\\bredmi\\s+12\\s+5g\\b.*")
                    && !t.contains("23076rn8dy")) return false;
            if (t.matches("(?s).*\\bredmi\\s+note\\b.*")
                    || t.matches("(?s).*\\bredmi\\s+12\\s+(?:4g|c|pro)\\b.*")
                    || t.matches("(?s).*\\b23076rn(?!8dy)[a-z0-9]+\\b.*")) return false;
        } else {
            String n = model.name.toLowerCase(Locale.ROOT);
            if (n.length() < 7 || !t.contains(n)) return false;
        }
        java.util.regex.Matcher storage = java.util.regex.Pattern.compile(
                "\\b(32|64|128|256|512|1024)\\s*(?:gb|gbyte)\\b",
                java.util.regex.Pattern.CASE_INSENSITIVE).matcher(t);
        boolean found = false;
        while (storage.find()) {
            if (Integer.parseInt(storage.group(1)) != model.storageGb) return false;
            found = true;
        }
        if (!found) return false;
        if (model.ramGb > 0) {
            java.util.regex.Matcher ram = java.util.regex.Pattern.compile(
                    "\\b(2|3|4|6|8|12|16)\\s*gb\\s*ram\\b|\\bram\\s*(2|3|4|6|8|12|16)\\s*gb\\b",
                    java.util.regex.Pattern.CASE_INSENSITIVE).matcher(t);
            while (ram.find()) {
                int gb = Integer.parseInt(ram.group(1) == null ? ram.group(2) : ram.group(1));
                if (gb != model.ramGb) return false;
            }
        }
        return true;
    }

    public static boolean validComparable(Model model, Listing listing, SaleCondition condition) {
        return listing != null && condition != null
                && listing.sellerCondition == condition
                && listing.askingCents > 0 && listing.askingCents <= 500000
                && listing.shippingCents >= 0 && listing.shippingCents <= 100000
                && isAllowedListingUrl(listing.url) && titleMatches(model, listing.title);
    }

    public static final class Summary {
        public final SaleCondition condition;
        public final int count;
        public final long minCents, medianCents, maxCents;
        Summary(SaleCondition condition, int count, long min, long median, long max) {
            this.condition = condition;
            this.count = count;
            this.minCents = min;
            this.medianCents = median;
            this.maxCents = max;
        }
        /** Require >=3 distinct listing URLs before quoting any median. */
        public boolean usable() { return count >= 3; }
    }

    public static Summary summarize(Model model, List<Listing> input, SaleCondition condition) {
        List<Long> prices = new ArrayList<>();
        Set<String> urls = new HashSet<>();
        if (input != null) for (Listing listing : input) {
            if (validComparable(model, listing, condition) && urls.add(listing.url))
                prices.add(listing.deliveredCents());
        }
        Collections.sort(prices);
        int n = prices.size();
        if (n == 0) return new Summary(condition, 0, 0, 0, 0);
        long med = (n % 2 == 1) ? prices.get(n / 2)
                : (prices.get(n / 2 - 1) + prices.get(n / 2)) / 2L;
        return new Summary(condition, n, prices.get(0), med, prices.get(n - 1));
    }

    public static long parseEuroCents(String input) {
        if (input == null) return -1;
        String x = input.trim().replace("€", "").trim();
        if (!x.matches("\\d{1,4}(?:[.,]\\d{1,2})?")) return -1;
        try {
            long cents = new BigDecimal(x.replace(',', '.'))
                    .movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact();
            return cents > 0 && cents <= 500000 ? cents : -1;
        } catch (ArithmeticException | NumberFormatException e) {
            return -1;
        }
    }

    public static String euro(long cents) {
        return String.format(Locale.GERMANY, "%.2f €", cents / 100d);
    }

    /**
     * Prices of USED, working-claimed devices and defect-claimed devices are
     * NOT GEL A+/A/B/C/D grades. A listing cannot be matched to another user's
     * GEL grade without an independently verifiable GEL report.
     */
    public static String describeGradeComparison(String localGrade,
            Summary working, Summary defective, boolean greek) {
        String grade = safe(localGrade);
        if (grade.isEmpty()) return greek
                ? "Δεν υπάρχει έγκυρος βαθμός GEL 27."
                : "No valid GEL Lab 27 grade.";
        String label = greek ? "Βαθμός αυτής της συσκευής: " : "This device's GEL grade: ";
        String text = label + (greek && "D".equals(grade) ? "Δ" : grade);
        if (working != null && working.usable()) {
            text += (greek ? "\\nΔιάμεση ζητούμενη τιμή δηλωμένων λειτουργικών: "
                           : "\\nMedian asking price of seller-claimed working devices: ")
                    + euro(working.medianCents);
        }
        if (defective != null && defective.usable()) {
            text += (greek ? "\\nΔιάμεση ζητούμενη τιμή δηλωμένων ελαττωματικών: "
                           : "\\nMedian asking price of seller-claimed faulty devices: ")
                    + euro(defective.medianCents);
        }
        return text + (greek
                ? "\\nΚΑΜΙΑ αγγελία δεν θεωρείται ίδιου βαθμού GEL χωρίς επαληθεύσιμο GEL Certification. "
                  + "Πρόκειται για ζητούμενες τιμές και δηλώσεις πωλητών, όχι πωλήσεις."
                : "\\nNO listing counts as an equivalent GEL-grade device without verifiable GEL Certification. "
                  + "These are asking prices and seller claims, not completed sales.");
    }
}
