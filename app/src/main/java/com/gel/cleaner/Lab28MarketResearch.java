package com.gel.cleaner;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Lab 28: inform, do not recommend.
 * Open real eBay search pages with listing photos, seller prices and descriptions.
 * No in-app marketplace data feed, invented prices, thumbnails, rankings or
 * automatic filtering based on the device's GEL grade.
 */
public final class Lab28MarketResearch {
    private Lab28MarketResearch() {}

    public static final class Model {
        public final String name;
        public final String code;

        public Model(String name, String code) {
            this.name = name == null ? "" : name.trim();
            this.code = code == null ? "" : code.trim();
        }

        public String searchQuery() {
            // Use the recognizable market model, not regional SKU, RAM, storage
            // capacity or user's private technical identifiers.
            return name.isEmpty() ? code : name;
        }
    }

    public static Model identify(String manufacturer, String androidCode, String officialName) {
        String code = androidCode == null ? "" : androidCode.trim();
        if ("23076RN8DY".equalsIgnoreCase(code))
            return new Model("Xiaomi Redmi 12 5G", code);
        String market = officialName == null ? "" :
                officialName.replaceAll("\\s*\\([^)]*\\)\\s*$", "").trim();
        if (market.isEmpty() || market.equalsIgnoreCase(code)) {
            String maker = manufacturer == null ? "" : manufacturer.trim();
            market = (maker + " " + code).trim();
        }
        return new Model(market, code);
    }

    /** eBay, not GEL, controls the listing order and the images on search pages. */
    public static String browseSearchUrl(Model model, boolean uk) {
        if (model == null || model.searchQuery().trim().isEmpty())
            throw new IllegalArgumentException("Missing searchable model identity");
        try {
            String domain = uk ? "https://www.ebay.co.uk" : "https://www.ebay.de";
            return domain + "/sch/i.html?_nkw="
                    + URLEncoder.encode(model.searchQuery(), StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException impossible) {
            throw new IllegalStateException(impossible);
        }
    }
}
