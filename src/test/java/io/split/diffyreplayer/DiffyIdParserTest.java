package io.split.diffyreplayer;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.regex.Pattern;

public class DiffyIdParserTest {

    @Test
    public void testNoIdWorks() {
        Map<Pattern, String> patterns = Maps.newHashMap();
        patterns.put(Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"), "ID");
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals("should/be/same", parser.convert("should/be/same"));
        Assert.assertEquals("shouldbesame", parser.convert("shouldbesame"));
        Assert.assertEquals("shouldbesame", parser.convert("shouldbesame/"));
        Assert.assertEquals("should/besame", parser.convert("/should/besame"));
    }

    @Test
    public void testIdWorks() {
        Map<Pattern, String> patterns = Maps.newHashMap();
        patterns.put(Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"), "ID");
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals("org/ID/env/ID/tests/ID",
                parser.convert("org/4d3405a0-9ca5-11e5-9706-16a11fb02dec/env/4fbab080-9ca5-11e5-9706-16a11fb02dec/tests/ec401d70-54fe-11e6-8857-068f59b32aa9/"));
        Assert.assertEquals("tests/ID",
                parser.convert("/tests/ec401d70-54fe-11e6-8857-068f59b32aa9"));
        Assert.assertEquals("ID",
                parser.convert("/ec401d70-54fe-11e6-8857-068f59b32aa9/"));
    }

    @Test
    public void testFixedWorks() {
        Map<Pattern, String> patterns = Maps.newHashMap();
        patterns.put(Pattern.compile("api/segmentChanges/[a-zA-Z][a-zA-Z0-9_-]*"), "api/segmentChanges/SEGMENT_NAME");
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals("api/segmentChanges/SEGMENT_NAME",
                parser.convert("api/segmentChanges/Demo"));
        Assert.assertEquals("api/segmentChanges/SEGMENT_NAME",
                parser.convert("/api/segmentChanges/Demo/"));
    }
}
