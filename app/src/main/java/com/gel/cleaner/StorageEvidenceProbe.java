package com.gel.cleaner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * Bounded, opt-in functional check of the app's private cache filesystem.
 * A successful check is NOT a NAND lifetime/health or controller certification.
 * Run on a worker thread. Writes no personal/user files.
 */
public final class StorageEvidenceProbe {
    private StorageEvidenceProbe() {}

    public static final int TEST_BYTES = 256 * 1024;
    private static final int BUFFER_BYTES = 8192;

    public static final class Result {
        public final boolean completed;
        public final boolean checksumMatched;
        public final int bytes;
        public final long writeAndSyncMs;
        public final long readMs;
        public final boolean cleanedUp;
        public final String reason;

        private Result(boolean completed, boolean checksumMatched, int bytes,
                       long writeAndSyncMs, long readMs, boolean cleanedUp, String reason) {
            this.completed = completed;
            this.checksumMatched = checksumMatched;
            this.bytes = bytes;
            this.writeAndSyncMs = writeAndSyncMs;
            this.readMs = readMs;
            this.cleanedUp = cleanedUp;
            this.reason = reason;
        }
    }

    public static Result run(File appCacheDirectory) {
        if (appCacheDirectory == null || !appCacheDirectory.isDirectory()) {
            return new Result(false, false, 0, 0, 0, true, "Private cache unavailable");
        }
        if (appCacheDirectory.getUsableSpace() < 4L * 1024L * 1024L) {
            return new Result(false, false, 0, 0, 0, true, "Insufficient free space");
        }

        File testFile = null;
        boolean verified = false;
        boolean finished = false;
        boolean cleanedUp = true;
        String reason = "Unknown";
        long writeMs = 0L;
        long readMs = 0L;
        int bytesRead = 0;

        try {
            testFile = File.createTempFile("gel_lab18_", ".tmp", appCacheDirectory);
            byte[] block = new byte[BUFFER_BYTES];
            new Random(System.nanoTime()).nextBytes(block);
            MessageDigest written = MessageDigest.getInstance("SHA-256");
            long writeStart = System.nanoTime();
            try (FileOutputStream output = new FileOutputStream(testFile)) {
                int writtenBytes = 0;
                while (writtenBytes < TEST_BYTES) {
                    int length = Math.min(block.length, TEST_BYTES - writtenBytes);
                    output.write(block, 0, length);
                    written.update(block, 0, length);
                    writtenBytes += length;
                }
                output.flush();
                output.getFD().sync();
            }
            writeMs = (System.nanoTime() - writeStart) / 1000000L;
            MessageDigest read = MessageDigest.getInstance("SHA-256");
            long readStart = System.nanoTime();
            try (FileInputStream input = new FileInputStream(testFile)) {
                int n;
                while ((n = input.read(block)) != -1) {
                    read.update(block, 0, n);
                    bytesRead += n;
                    if (bytesRead > TEST_BYTES) break;
                }
            }
            readMs = (System.nanoTime() - readStart) / 1000000L;
            verified = bytesRead == TEST_BYTES
                    && Arrays.equals(written.digest(), read.digest());
            finished = true;
            reason = verified ? "Sandbox read/write checksum matched"
                    : "Sandbox read/write checksum mismatch";
        } catch (IOException | NoSuchAlgorithmException | SecurityException e) {
            reason = "Sandbox storage test could not complete: " + e.getClass().getSimpleName();
        } finally {
            if (testFile != null && testFile.exists()) cleanedUp = testFile.delete();
        }
        // Cleanup failure is explicitly exposed; this is not a NAND wear conclusion.
        return new Result(finished, verified, bytesRead, writeMs, readMs, cleanedUp, reason);
    }
}
