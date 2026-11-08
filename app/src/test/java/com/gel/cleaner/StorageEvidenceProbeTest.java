package com.gel.cleaner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class StorageEvidenceProbeTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test public void boundedSandboxProbeChecksChecksumAndRemovesTemporaryFile() throws Exception {
        StorageEvidenceProbe.Result r = StorageEvidenceProbe.run(folder.getRoot());
        assertTrue(r.reason, r.completed);
        assertTrue(r.reason, r.checksumMatched);
        assertEquals(StorageEvidenceProbe.TEST_BYTES, r.bytes);
        assertTrue(r.cleanedUp);
        assertEquals(0, folder.getRoot().listFiles().length);
        assertTrue(r.writeAndSyncMs >= 0);
        assertTrue(r.readMs >= 0);
    }

    @Test public void missingCachePathIsUnknownNotHealthy() {
        StorageEvidenceProbe.Result r = StorageEvidenceProbe.run(null);
        assertFalse(r.completed);
        assertFalse(r.checksumMatched);
    }
}
