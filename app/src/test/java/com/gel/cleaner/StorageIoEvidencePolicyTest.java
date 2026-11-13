package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StorageIoEvidencePolicyTest {
    @Test public void completeByteForByteReadbackPasses() {
        assertTrue(StorageIoEvidencePolicy.readbackMatches(
                new byte[]{1, 2, 3}, new byte[]{1, 2, 3}, 3));
    }

    @Test public void truncatedOrMismatchedReadbackIsNotSuccess() {
        assertFalse(StorageIoEvidencePolicy.readbackMatches(
                new byte[]{1, 2, 3}, new byte[]{1, 2, 3}, 2));
        assertFalse(StorageIoEvidencePolicy.readbackMatches(
                new byte[]{1, 2, 3}, new byte[]{1, 9, 3}, 3));
        assertFalse(StorageIoEvidencePolicy.readbackMatches(
                new byte[]{1, 2, 3}, new byte[]{1, 2}, 2));
    }

    @Test public void unknownAndEmptyBuffersAreNotEvidence() {
        assertFalse(StorageIoEvidencePolicy.readbackMatches(null, new byte[]{1}, 1));
        assertFalse(StorageIoEvidencePolicy.readbackMatches(new byte[]{1}, null, 1));
        assertFalse(StorageIoEvidencePolicy.readbackMatches(new byte[0], new byte[0], 0));
    }
}
