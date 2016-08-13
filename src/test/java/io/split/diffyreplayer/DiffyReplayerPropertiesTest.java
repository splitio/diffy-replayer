package io.split.diffyreplayer;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class DiffyReplayerPropertiesTest {

    @Test
    public void testCanParsePatternFile() throws IOException {
        Map<Pattern, String> patterns = DiffyReplayerProperties.INSTANCE.getPatterns();
        Assert.assertEquals(3, patterns.size());
    }
}
