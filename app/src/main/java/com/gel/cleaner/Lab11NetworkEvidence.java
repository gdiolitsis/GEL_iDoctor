package com.gel.cleaner;

/**
 * Lab 11: classify Android's ACTIVE DEFAULT route, not radio/SIM hardware
 * health. NET_CAPABILITY_INTERNET signals intent; VALIDATED is an OS-reported
 * connectivity observation, never proof of modem/cell-tower reliability.
 */
final class Lab11NetworkEvidence {
    enum DefaultRoute {
        NO_DEFAULT_NETWORK,
        CAPABILITIES_UNAVAILABLE,
        CELLULAR_VALIDATED,
        CELLULAR_NOT_VALIDATED,
        WIFI_VALIDATED,
        WIFI_NOT_VALIDATED,
        VPN_VALIDATED_UNATTRIBUTED,
        VPN_NOT_VALIDATED_UNATTRIBUTED,
        OTHER_VALIDATED,
        OTHER_NOT_VALIDATED
    }

    private Lab11NetworkEvidence() {}

    static DefaultRoute classifyDefault(boolean hasNetwork, boolean hasCaps,
                                         boolean advertisesInternet,
                                         boolean validated,
                                         boolean cellular, boolean wifi,
                                         boolean vpn) {
        if (!hasNetwork) return DefaultRoute.NO_DEFAULT_NETWORK;
        if (!hasCaps) return DefaultRoute.CAPABILITIES_UNAVAILABLE;

        boolean verified = advertisesInternet && validated;
        // VPN can use cellular, Wi-Fi, or both underneath; cannot attribute
        // the default network's validation to one physical radio.
        if (vpn) return verified
                ? DefaultRoute.VPN_VALIDATED_UNATTRIBUTED
                : DefaultRoute.VPN_NOT_VALIDATED_UNATTRIBUTED;
        // Mixed transports are deliberately not labeled as direct cellular.
        if (cellular && !wifi) return verified
                ? DefaultRoute.CELLULAR_VALIDATED
                : DefaultRoute.CELLULAR_NOT_VALIDATED;
        if (wifi && !cellular) return verified
                ? DefaultRoute.WIFI_VALIDATED
                : DefaultRoute.WIFI_NOT_VALIDATED;
        return verified
                ? DefaultRoute.OTHER_VALIDATED
                : DefaultRoute.OTHER_NOT_VALIDATED;
    }

    static boolean provesCurrentDefaultCellularValidation(DefaultRoute r) {
        return r == DefaultRoute.CELLULAR_VALIDATED;
    }

    static boolean isServiceObservationKnown(boolean phoneApiAvailable,
                                              boolean serviceReadSucceeded) {
        return phoneApiAvailable && serviceReadSucceeded;
    }


}
