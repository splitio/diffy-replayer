package io.split.diffyreplayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MultivaluedHashMap;
import java.io.IOException;
import java.lang.reflect.Method;

public class DiffyReplayerFilterTest {

    private ResourceInfo resourceInfo;
    private DiffyReplayer diffyReplayer;
    private ContainerRequestContext requestContext;
    private MultivaluedHashMap<String, String> headers;
    private DiffyReplayerFilter diffyReplayerFilter;
    private Method replayedMethod;

    @Before
    public void setUp() throws NoSuchMethodException {
        resourceInfo = Mockito.mock(ResourceInfo.class);
        diffyReplayer = Mockito.mock(DiffyReplayer.class);
        requestContext = Mockito.mock(ContainerRequestContext.class);
        headers = new MultivaluedHashMap<>();
        diffyReplayerFilter = new DiffyReplayerFilter(resourceInfo, diffyReplayer);

        Mockito.when(requestContext.getHeaders())
                .thenReturn(headers);
        Mockito.when(requestContext.getMethod())
                .thenReturn("GET");

    }

    @Test
    public void checkRequestWithDiffyHeaderDoesNotGetReplayed() throws IOException {
        headers.putSingle(DiffyReplay.HEADER, "");

        diffyReplayerFilter
                .filter(requestContext);
        Mockito.verify(diffyReplayer, Mockito.never()).replay(Mockito.anyObject(), Mockito.anyObject());
    }

    @Test
    public void checkPostPutAndDeleteDoNotGetReplayed() throws IOException {
        Mockito.when(requestContext.getMethod())
                .thenReturn("POST");
        diffyReplayerFilter
                .filter(requestContext);
        Mockito.verify(diffyReplayer, Mockito.never()).replay(Mockito.anyObject(), Mockito.anyObject());

        Mockito.when(requestContext.getMethod())
                .thenReturn("DELETE");
        diffyReplayerFilter
                .filter(requestContext);
        Mockito.verify(diffyReplayer, Mockito.never()).replay(Mockito.anyObject(), Mockito.anyObject());

        Mockito.when(requestContext.getMethod())
                .thenReturn("PUT");
        diffyReplayerFilter = new DiffyReplayerFilter(resourceInfo, diffyReplayer);
        diffyReplayerFilter
                .filter(requestContext);
        Mockito.verify(diffyReplayer, Mockito.never()).replay(Mockito.anyObject(), Mockito.anyObject());
    }

    @Test
    public void methodGetsReplayedWithClassAnnotation() throws IOException {
        WithDiffyReplay withDiffyReplay = new WithDiffyReplay();
        Mockito.when(resourceInfo.getResourceClass())
                .then(invocationOnMock -> withDiffyReplay.getClass());
        Mockito.when(resourceInfo.getResourceMethod())
                .then(invocationOnMock -> withDiffyReplay.getClass().getMethod("withoutAnnotation"));

        diffyReplayerFilter
                .filter(requestContext);
        Mockito.verify(diffyReplayer, Mockito.only()).replay(Mockito.anyObject(), Mockito.isA(DiffyAnnotationClass.class));
    }
    
}
