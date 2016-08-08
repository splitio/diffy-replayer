package io.split.diffyreplayer;

import com.google.common.base.Preconditions;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * In charge of parsing the endpoint URI and transforming it if it matches a pattern defined in the patterns file.
 */
public class DiffyIdParser {

    private final Map<Pattern, String> patterns;

    /**
     * Default Constructor
     *
     * @param patterns the map of patterns that are going to be used to replace the URIS
     */
    public DiffyIdParser(Map<Pattern, String> patterns) {
        this.patterns = Preconditions.checkNotNull(patterns);
    }

    /**
     * Converts a URI and checks if it matches a pattern.
     *
     * @param uri the URI to parse.
     * @return the parsed and converted response.
     */
    public String convert(String uri) {
        // First we remove starting and trailing "/", to homogenize the Diffy dashboard
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length()-1);
        }
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        //Simply check if a patter applies.
        for(Map.Entry<Pattern, String> entry : patterns.entrySet()) {
            uri = entry.getKey().matcher(uri).replaceAll(entry.getValue());
        }
        return uri;
    }
}
