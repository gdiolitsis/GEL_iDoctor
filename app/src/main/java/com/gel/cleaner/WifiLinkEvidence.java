package com.gel.cleaner;

/**
 * Android-reported Wi-Fi link context. This deliberately does not simulate,
 * extrapolate or claim a measured Internet data-plane throughput.
 */
public final class WifiLinkEvidence {
    public final int phyRateMbps;
    public final int rssiDbm;

    private WifiLinkEvidence(int phyRateMbps, int rssiDbm) {
        this.phyRateMbps = phyRateMbps;
        this.rssiDbm = rssiDbm;
    }

    public static WifiLinkEvidence fromAndroid(int phyRateMbps, int rssiDbm) {
        return new WifiLinkEvidence(phyRateMbps > 0 ? phyRateMbps : -1,
                rssiDbm >= -110 && rssiDbm <= -20 ? rssiDbm : 127);
    }

    public boolean hasPhyRate() { return phyRateMbps > 0; }
    public boolean hasRssi() { return rssiDbm != 127; }
    public boolean hasMeasuredInternetThroughput() { return false; }
}
