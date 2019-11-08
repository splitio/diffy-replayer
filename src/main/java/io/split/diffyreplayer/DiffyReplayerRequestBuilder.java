package io.split.diffyreplayer;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.split.diffyreplayer.util.ContainerRequestUtil;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import javax.ws.rs.container.ContainerRequestContext;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Builds the request to be sent to Diffy.
 */
public class DiffyReplayerRequestBuilder {

    // Used so the Diffy UI displays the right name on the list.
    private static final String CANONICAL_RESOURCE = "Canonical-Resource";

    private final URL destinationURL;
    private final DiffyIdParser idParser;

    /**
     * Default Constructor.
     *
     * @param destinationURL Where the Diffy Server resides.
     */
    public DiffyReplayerRequestBuilder(String destinationURL) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(destinationURL));
        this.idParser = new DiffyIdParser(DiffyReplayerProperties.getInstance().getPatterns());
        this.destinationURL = getURLOrBlow(destinationURL);
    }

    /**
     * Builds the DiffyRequest that will be send to the Diffy Server.
     *
     * Adds the headers so the same query is not re-replayed.
     *
     * @param original the Original request to be replayed.
     * @return a Copy of the original request that points to Diffy Server.
     */
    public HttpRequestBase build(ContainerRequestContext original) {
        Preconditions.checkNotNull(original);

        String pathWithQueryParams = ContainerRequestUtil.getPathWithQueryParams(original);
        if (!"GET".equals(original.getMethod())) {
            throw new IllegalArgumentException(String.format("Only GETS are allowed, method %s is is %s",
                    pathWithQueryParams, original.getMethod()));
        }

        HttpGet get = new HttpGet(getURLOrBlow(destinationURL, pathWithQueryParams).toExternalForm());
        addHeaders(original, get);
        return get;
    }

    /**
     * Adds the headers of the original request to the destination requests.
     *
     * Also adds the DiffyReplay header so it is not replayed.
     * Also adds a header so the Diffy UI shows the right name.
     *
     * @param original the original request to be replayed.
     * @param destination the get request where the new headers are going to be.
     */
    private void addHeaders(ContainerRequestContext original, HttpGet destination) {
        Preconditions.checkNotNull(original);
        Preconditions.checkNotNull(destination);

        destination.addHeader(CANONICAL_RESOURCE, idParser.convert(original.getUriInfo().getRequestUri().getRawPath()));
        destination.addHeader(DiffyReplay.HEADER, "true");
        original.getHeaders()
                .forEach((header, list) ->
                        list.forEach(value -> destination.addHeader(header, value)));
    }

    private URL getURLOrBlow(String url) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url));

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("URL %s is not valid", url), e);
        }
    }

    private URL getURLOrBlow(URL base, String pathWithQueryParams) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(pathWithQueryParams));
        Preconditions.checkNotNull(base);

        try {

            return new URL(base, pathWithQueryParams);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("URL %s is not valid", pathWithQueryParams), e);
        }
    }
}
