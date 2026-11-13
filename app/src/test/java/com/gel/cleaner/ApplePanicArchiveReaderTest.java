package com.gel.cleaner;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

public class ApplePanicArchiveReaderTest {
    private static byte[] zipped(String[] names, String[] contents) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(buf)) {
            for (int i = 0; i < names.length; i++) {
                zip.putNextEntry(new ZipEntry(names[i]));
                if (contents[i] != null) zip.write(contents[i].getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
        }
        return buf.toByteArray();
    }
    private static String log(String reason) {
        return "Date: 2026-11-01; panic(cpu 0 caller 0xfffffff): " + reason + " Device iPhone17,4";
    }
    private static ApplePanicArchiveReader.Archive read(byte[] bytes) throws Exception {
        return ApplePanicArchiveReader.read(new ByteArrayInputStream(bytes), 12, 3000, 6000);
    }
    @Test public void readsAllValidEntriesWithoutClosingParentAfterFirst() throws Exception {
        byte[] bytes=zipped(
            new String[]{"panic-1.ips","notes.txt","folder/","panic-2.log"},
            new String[]{log("watchdog"),"Jetsam memorystatus highwater reported",null,log("kernel")});
        ApplePanicArchiveReader.Archive archive=read(bytes);
        assertEquals(2,archive.eligibleEntries);
        assertTrue(archive.text.contains("panic-1.ips"));
        assertTrue(archive.text.contains("panic-2.log"));
        assertFalse(archive.text.contains("Jetsam memorystatus highwater"));
    }
    @Test public void acceptsBugType210ButRejectsUnrelatedReports() throws Exception {
        ApplePanicArchiveReader.Archive archive=read(zipped(
            new String[]{"system-1.ips","system-2.ips"},
            new String[]{"timestamp 2026: bug_type: 210; deviceModel iPhone18,2", "2026 thermal event. no panic structure here."}));
        assertEquals(1,archive.eligibleEntries);
        assertTrue(archive.text.contains("bug_type: 210"));
    }
    @Test public void refusesZipWithoutSupportedPanic() throws Exception {
        byte[] bytes=zipped(new String[]{"jetsam.ips","thermal.txt"},
            new String[]{"jetsam highwater memory", "generic temperature events"});
        IOException e=assertThrows(IOException.class, ()->read(bytes));
        assertTrue(e.getMessage().contains("No supported panic"));
    }
    @Test public void enforcesEntryCountEvenWhenMostlyUnrelated() throws Exception {
        byte[] bytes=zipped(new String[]{"a.bin","b.bin","c.ips"},
            new String[]{"small","small",log("real panic")});
        assertTrue(assertThrows(IOException.class,()-> ApplePanicArchiveReader.read(
            new ByteArrayInputStream(bytes),2,3000,6000)).getMessage().contains("entry limit"));
    }
    @Test public void rejectsOversizeCandidateBeforeDiagnosticClaims() throws Exception {
        byte[] bytes=zipped(new String[]{"panic-big.ips"},new String[]{log("long")+"X".repeat(3200)});
        assertTrue(assertThrows(IOException.class,()->read(bytes)).getMessage().contains("size cap"));
    }
    @Test public void rejectsOversizeUnrelatedEntryToo() throws Exception {
        byte[] bytes=zipped(new String[]{"random.bin","panic-ok.ips"},
            new String[]{"X".repeat(3200), log("valid")});
        assertTrue(assertThrows(IOException.class,()->read(bytes)).getMessage().contains("size cap"));
    }
    @Test public void enforcesTotalDecompressedTextLimit() throws Exception {
        byte[] bytes=zipped(new String[]{"a.ips","b.ips"},
            new String[]{log("one")+"x".repeat(180),log("two")+"y".repeat(180)});
        assertTrue(assertThrows(IOException.class,()->ApplePanicArchiveReader.read(
            new ByteArrayInputStream(bytes),12,3000,300)).getMessage().contains("Combined"));
    }
    @Test public void doesNotEchoEntryFilenameHtmlOrNewlines() throws Exception {
        byte[] bytes=zipped(new String[]{"nested/<i>panic</i>.ips"},new String[]{log("valid")});
        String text=read(bytes).text;
        assertFalse(text.contains("<i>"));
        assertTrue(text.contains("panic"));
    }
}
