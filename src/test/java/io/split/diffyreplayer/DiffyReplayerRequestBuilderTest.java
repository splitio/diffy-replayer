package io.split.diffyreplayer;

import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;

public class DiffyReplayerRequestBuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ContainerRequestContext requestContext;
    private MultivaluedHashMap<String, String> headers;

    @Before
    public void setUp() throws NoSuchMethodException, URISyntaxException {
        requestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(requestContext.getMethod())
                .thenReturn("GET");
        URI uri = new URI("https://sdk-staging.split.io/api/qa/testDiffy");
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        headers = new MultivaluedHashMap<>();
        headers.putSingle("firstKey", "firstValue");
        headers.putSingle("secondKey", "secondValue");
        Mockito.when(requestContext.getHeaders())
                .thenReturn(headers);

        Mockito.when(requestContext.getUriInfo())
                .thenReturn(uriInfo);
        Mockito.when(uriInfo.getRequestUri())
                .thenReturn(uri);


    }

    @Test
    public void headersAreCopiedAndRequestIsPointedToServer() {
        DiffyReplayerRequestBuilder test = new DiffyReplayerRequestBuilder("https://diffy-server.io");
        HttpRequestBase requestBase = test.build(requestContext);
        Assert.assertEquals("https://diffy-server.io/api/qa/testDiffy", requestBase.getURI().toString());
        Assert.assertEquals(4, requestBase.getAllHeaders().length);
        Assert.assertEquals("api/qa/testDiffy", requestBase.getFirstHeader("Canonical-Resource").getValue());
        Assert.assertEquals("true", requestBase.getFirstHeader("replayer").getValue());
        Assert.assertEquals("firstValue", requestBase.getFirstHeader("firstKey").getValue());
        Assert.assertEquals("secondValue", requestBase.getFirstHeader("secondKey").getValue());
    }

    @Test
    public void doesNotAllowPost() {
        Mockito.when(requestContext.getMethod())
                .thenReturn("POST");
        DiffyReplayerRequestBuilder test = new DiffyReplayerRequestBuilder("https://diffy-server.io");
        expectedException.expect(IllegalArgumentException.class);
        test.build(requestContext);

    }

    @Test
    public void doesNotAllowDelete() {
        Mockito.when(requestContext.getMethod())
                .thenReturn("DELETE");
        DiffyReplayerRequestBuilder test = new DiffyReplayerRequestBuilder("https://diffy-server.io");
        expectedException.expect(IllegalArgumentException.class);
        test.build(requestContext);

    }

    @Test
    public void doesNotAllowPut() {
        Mockito.when(requestContext.getMethod())
                .thenReturn("PUT");
        DiffyReplayerRequestBuilder test = new DiffyReplayerRequestBuilder("https://diffy-server.io");
        expectedException.expect(IllegalArgumentException.class);
        test.build(requestContext);

    }

}
