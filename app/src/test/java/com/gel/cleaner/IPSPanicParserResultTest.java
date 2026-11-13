package com.gel.cleaner;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

public class IPSPanicParserResultTest {
    @Test public void primaryFieldsRemainVisibleToLegacyLab1() {
        IPSPanicParser.Result best = new IPSPanicParser.Result();
        best.patternId = "panic.test"; best.domain = "storage"; best.cause = "unverified token";
        best.severity = "HIGH"; best.confidence = "High"; best.recommendation = "technician review";
        IPSPanicParser.Result second = new IPSPanicParser.Result();
        second.patternId = "second";
        IPSPanicParser.Result actual = IPSPanicParser.assembleMatches(Arrays.asList(best, second));
        assertSame(best, actual.primary);
        assertEquals("panic.test", actual.patternId);
        assertEquals("storage", actual.domain);
        assertEquals("unverified token", actual.cause);
        assertEquals("HIGH", actual.severity);
        assertEquals("High", actual.confidence);
        assertEquals("technician review", actual.recommendation);
        assertEquals(1, actual.secondary.size());
        assertSame(second, actual.secondary.get(0));
    }
    @Test public void emptyMatchesRemainUnavailable() {
        assertNull(IPSPanicParser.assembleMatches(Collections.emptyList()));
        assertNull(IPSPanicParser.assembleMatches(null));
    }
}
