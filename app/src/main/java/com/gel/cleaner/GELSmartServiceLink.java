// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELSmartServiceLink.java
// Smart Service QR transport helper.
// One QR can open iDoctor when installed or fall back to installation.

package com.gel.cleaner;

import android.net.Uri;

final class GELSmartServiceLink {

    static final String SMART_HOST = "gel-idoctor.web.app";
    static final String SMART_PATH = "/connect";

    private GELSmartServiceLink() {
    }

    static String buildSmartLink(
            String sessionId,
            String serviceCode,
            long expiresAt
    ) {

        return new Uri.Builder()
                .scheme("https")
                .authority(SMART_HOST)
                .path(SMART_PATH)
                .appendQueryParameter("v", "1")
                .appendQueryParameter("session", safe(sessionId))
                .appendQueryParameter("code", safe(serviceCode))
                .appendQueryParameter("expires", String.valueOf(expiresAt))
                .build()
                .toString();
    }

    static String buildLegacyLink(
            String sessionId,
            String serviceCode,
            long expiresAt
    ) {

        return new Uri.Builder()
                .scheme("gel")
                .authority("technician")
                .path("pair")
                .appendQueryParameter("v", "1")
                .appendQueryParameter("session", safe(sessionId))
                .appendQueryParameter("code", safe(serviceCode))
                .appendQueryParameter("expires", String.valueOf(expiresAt))
                .build()
                .toString();
    }

    static ParsedLink parse(String payload) {

        if (payload == null || payload.trim().isEmpty()) {
            return null;
        }

        try {

            Uri uri = Uri.parse(payload.trim());

            String scheme = uri.getScheme();
            String host = uri.getHost();
            String path = uri.getPath();

            boolean legacy =
                    "gel".equalsIgnoreCase(scheme) &&
                            "technician".equalsIgnoreCase(host) &&
                            "/pair".equals(path);

            boolean smartHttps =
                    "https".equalsIgnoreCase(scheme) &&
                            SMART_HOST.equalsIgnoreCase(host) &&
                            (SMART_PATH.equals(path) || (SMART_PATH + "/").equals(path));

            if (!legacy && !smartHttps) {
                return null;
            }

            String version = uri.getQueryParameter("v");
            String sessionId = uri.getQueryParameter("session");
            String serviceCode = uri.getQueryParameter("code");
            String expiresRaw = uri.getQueryParameter("expires");

            if (!"1".equals(version) ||
                    sessionId == null ||
                    !sessionId.startsWith("GEL-") ||
                    !isSixDigitCode(serviceCode) ||
                    expiresRaw == null) {

                return null;
            }

            long expiresAt = Long.parseLong(expiresRaw);

            if (expiresAt <= 0L) {
                return null;
            }

            return new ParsedLink(
                    sessionId,
                    serviceCode,
                    expiresAt
            );

        } catch (Throwable ignored) {
            return null;
        }
    }

    static String smartPayloadFromInstallReferrer(String rawReferrer) {

        if (rawReferrer == null || rawReferrer.trim().isEmpty()) {
            return null;
        }

        String candidate = rawReferrer.trim();

        // Some Play Store / browser paths return the whole referrer URL-encoded.
        // Decode at most once; Uri parsing below handles the query values safely.
        try {
            candidate = Uri.decode(candidate);
        } catch (Throwable ignored) {
        }

        if (candidate.startsWith("https://") ||
                candidate.startsWith("gel://")) {

            return parse(candidate) != null
                    ? candidate
                    : null;
        }

        try {

            Uri referrerUri = Uri.parse(
                    "https://" + SMART_HOST + SMART_PATH + "?" + candidate
            );

            String marker = referrerUri.getQueryParameter("gel_service");

            if (!"1".equals(marker)) {
                return null;
            }

            String sessionId = referrerUri.getQueryParameter("session");
            String serviceCode = referrerUri.getQueryParameter("code");
            String expiresRaw = referrerUri.getQueryParameter("expires");
            String version = referrerUri.getQueryParameter("v");

            if (!"1".equals(version) ||
                    sessionId == null ||
                    !sessionId.startsWith("GEL-") ||
                    !isSixDigitCode(serviceCode) ||
                    expiresRaw == null) {

                return null;
            }

            long expiresAt = Long.parseLong(expiresRaw);

            String payload = buildSmartLink(
                    sessionId,
                    serviceCode,
                    expiresAt
            );

            return parse(payload) != null
                    ? payload
                    : null;

        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean isSixDigitCode(String code) {

        if (code == null || code.length() != 6) {
            return false;
        }

        for (int i = 0; i < code.length(); i++) {
            if (!Character.isDigit(code.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    static final class ParsedLink {

        final String sessionId;
        final String serviceCode;
        final long expiresAt;

        ParsedLink(
                String sessionId,
                String serviceCode,
                long expiresAt
        ) {
            this.sessionId = sessionId;
            this.serviceCode = serviceCode;
            this.expiresAt = expiresAt;
        }
    }
}
