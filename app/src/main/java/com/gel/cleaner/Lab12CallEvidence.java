package com.gel.cleaner;

/**
 * Lab 12 evidence is strictly separated into Android-reported capability /
 * service observations and optional subjective two-way call feedback.
 * Neither category independently certifies telephony hardware or IMS.
 */
final class Lab12CallEvidence {
    enum Passive {
        TELEPHONY_UNAVAILABLE,
        AIRPLANE_MODE_REPORTED,
        VOICE_CAPABILITY_NOT_REPORTED,
        SERVICE_REPORTED_IN_SERVICE,
        SERVICE_NOT_REPORTED_IN_SERVICE,
        SERVICE_UNKNOWN
    }

    enum Guided {
        NOT_TESTED,
        INCONCLUSIVE,
        USER_REPORTED_TWO_WAY_AUDIO,
        USER_REPORTED_ONE_WAY_OR_AUDIO_ISSUE
    }

    private Lab12CallEvidence() {}

    static Passive passive(boolean telephonyAvailable,
                           Boolean airplaneOn,
                           Boolean voiceCapable,
                           Boolean reportedInService) {
        if (!telephonyAvailable) return Passive.TELEPHONY_UNAVAILABLE;
        if (Boolean.TRUE.equals(airplaneOn)) {
            return Passive.AIRPLANE_MODE_REPORTED;
        }
        if (Boolean.FALSE.equals(voiceCapable)) {
            return Passive.VOICE_CAPABILITY_NOT_REPORTED;
        }
        if (reportedInService == null) {
            return Passive.SERVICE_UNKNOWN;
        }
        return reportedInService
                ? Passive.SERVICE_REPORTED_IN_SERVICE
                : Passive.SERVICE_NOT_REPORTED_IN_SERVICE;
    }

    static Guided userReport(boolean dialerLaunched,
                             Boolean userCompletedRealCall,
                             Boolean heardRemote,
                             Boolean remoteHeardUser) {
        if (!dialerLaunched || Boolean.FALSE.equals(userCompletedRealCall)) {
            return Guided.NOT_TESTED;
        }
        if (userCompletedRealCall == null
                || heardRemote == null || remoteHeardUser == null) {
            return Guided.INCONCLUSIVE;
        }
        return heardRemote && remoteHeardUser
                ? Guided.USER_REPORTED_TWO_WAY_AUDIO
                : Guided.USER_REPORTED_ONE_WAY_OR_AUDIO_ISSUE;
    }
}
