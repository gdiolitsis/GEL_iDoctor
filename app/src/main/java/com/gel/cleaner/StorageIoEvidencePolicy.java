package com.gel.cleaner;

import java.util.Arrays;

/** Scope: a single complete app-private file round-trip, not NAND health. */
public final class StorageIoEvidencePolicy {
    private StorageIoEvidencePolicy() {}

    public static boolean readbackMatches(byte[] written, byte[] readBack, int bytesRead) {
        return written != null
                && readBack != null
                && written.length > 0
                && written.length == readBack.length
                && bytesRead == written.length
                && Arrays.equals(written, readBack);
    }
}
