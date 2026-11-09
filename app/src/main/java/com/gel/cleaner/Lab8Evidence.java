package com.gel.cleaner;

/** Evidence for the bounded Camera2 guided preview challenge.
 * Camera frames + a user's visual response do not certify lens/AF/ISP hardware.
 */
final class Lab8Evidence {
    enum Result {
        OBSERVED_PREVIEW_WITH_USER_CONFIRMATION,
        USER_REPORTED_NO_IMAGE,
        INCONCLUSIVE,
        UNAVAILABLE
    }
    private Lab8Evidence() {}

    static Result classify(boolean repeatingRequestStarted,
                           boolean sampleWindowFinished,
                           long frames,
                           long validLumaFrames,
                           Boolean userSawLivePreview) {
        if (!repeatingRequestStarted) {
            return Result.UNAVAILABLE;
        }
        if (!sampleWindowFinished || frames <= 0L
                || validLumaFrames <= 0L || validLumaFrames > frames) {
            return Result.INCONCLUSIVE;
        }
        if (userSawLivePreview == null) {
            return Result.INCONCLUSIVE;
        }
        return userSawLivePreview
                ? Result.OBSERVED_PREVIEW_WITH_USER_CONFIRMATION
                : Result.USER_REPORTED_NO_IMAGE;
    }
}
