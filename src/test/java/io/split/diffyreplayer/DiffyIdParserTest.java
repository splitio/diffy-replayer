package io.split.diffyreplayer;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class DiffyIdParserTest {

    @Test
    public void testNoIdWorks() {
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"), "ID");
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals("should/be/same", parser.convert("should/be/same"));
        Assert.assertEquals("shouldbesame", parser.convert("shouldbesame"));
        Assert.assertEquals("shouldbesame", parser.convert("shouldbesame/"));
        Assert.assertEquals("should/besame", parser.convert("/should/besame"));
    }

    @Test
    public void testIdWorks() {
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
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
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(Pattern.compile("api/segmentChanges/[a-zA-Z][@\\.a-zA-Z0-9_-]*"), "api/segmentChanges/SEGMENT_NAME");
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals("api/segmentChanges/SEGMENT_NAME",
                parser.convert("api/segmentChanges/Demo.343"));
        Assert.assertEquals("api/segmentChanges/SEGMENT_NAME",
                parser.convert("api/segmentChanges/Demo@test.com"));
        Assert.assertEquals("api/segmentChanges/SEGMENT_NAME",
                parser.convert("/api/segmentChanges/Demo/"));
    }

    @Test
    public void testWorksOne() {
        String expect = "internal/api/segments/organization/UUID/environment/UUID/segmentName/SEGMENT_NAME";
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(
                Pattern.compile("internal/api/segments/organization/UUID/environment/UUID/segmentName/[a-zA-Z][a-zA-Z0-9_-]*"),
                expect);
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals(expect,
                parser.convert("internal/api/segments/organization/UUID/environment/UUID/segmentName/thesegment"));
        Assert.assertEquals(expect,
                parser.convert("internal/api/segments/organization/UUID/environment/UUID/segmentName/PHP_5_3_addAndRemoveIdToSegmentAndCheckTestIsUpdated"));
    }

    @Test
    public void testWorksTwo() {
        String expect = "objectType/OBJECT_TYPE/objectId/UUID";
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(
                Pattern.compile("objectType/[a-zA-Z][a-zA-Z0-9_-]*/objectId/UUID"),
                expect);
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals(expect,
                parser.convert("objectType/Test/objectId/UUID/"));
        Assert.assertEquals(expect,
                parser.convert("objectType/Another_Name/objectId/UUID/"));
    }

    @Test
    public void testWorksThree() {
        String expect = "trafficType/{orgId}/TRAFFICE_NAME";
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(
                Pattern.compile("trafficType/UUID/[a-zA-Z][a-zA-Z0-9_-]*"),
                expect);
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals(expect,
                parser.convert("trafficType/UUID/user"));
        Assert.assertEquals(expect,
                parser.convert("trafficType/UUID/account/"));
    }

    @Test
    public void testWorksFour() {
        String expect = "internal/api/syntax/language/LANGUAGE/UUID";
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(
                Pattern.compile("internal/api/syntax/language/[a-zA-Z][a-zA-Z0-9_-]*/UUID"),
                expect);
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals(expect,
                parser.convert("internal/api/syntax/language/java/UUID"));
        Assert.assertEquals(expect,
                parser.convert("internal/api/syntax/language/nodejs/UUID/"));
    }

    @Test
    public void testWorksEmail() {
        String expect = "internal/api/invites/email/EMAIL/organization/UUID";
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(
                Pattern.compile("internal/api/invites/email/[A-Za-z0-9+_.-]+@(.+)/organization/UUID"),
                expect);
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals(expect,
                parser.convert("internal/api/invites/email/fernando@split.io/organization/UUID"));
        Assert.assertEquals(expect,
                parser.convert("internal/api/invites/email/fernando.a.martin+123@gmail.com/organization/UUID"));
    }

    @Test
    public void testRemoveStartingString() {

        String expect = "invites/email/theRest";
        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(
                Pattern.compile("^internal\\/api/"),
                "");
        DiffyIdParser parser = new DiffyIdParser(patterns);
        Assert.assertEquals(expect,
                parser.convert("internal/api/invites/email/theRest"));
        Assert.assertEquals(expect,
                parser.convert("/internal/api/invites/email/theRest"));
    }

    @Test
    public void testMultiple() {

        Map<Pattern, String> patterns = Maps.newLinkedHashMap();
        patterns.put(
                Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"),
                "UUID");
        patterns.put(
                Pattern.compile("^internal\\/api/"),
                "");
        patterns.put(
                Pattern.compile("segments/organization/UUID/environment/UUID/segmentName/[a-zA-Z][a-zA-Z0-9_-]*"),
                "segments/organization/UUID/environment/UUID/segmentName/SEGMENT_NAME");
        DiffyIdParser parser = new DiffyIdParser(patterns);
        String expect = "segments/organization/UUID/environment/UUID/segmentName/SEGMENT_NAME";
        Assert.assertEquals(expect,
                parser.convert("/internal/api/segments/organization/" + UUID.randomUUID().toString() +  "/environment/" + UUID.randomUUID().toString() + "/segmentName/theName"));



    }
}
