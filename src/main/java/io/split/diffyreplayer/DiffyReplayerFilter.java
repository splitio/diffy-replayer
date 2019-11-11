package io.split.diffyreplayer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 * This is where it starts.
 *
 * All requests that come (annotated with DiffyReplayer) will be filtered by this
 * ContainerRequestFilter.
 *
 * This only filters the requests that already have a DiffyReplayer header, so we don't
 * replay the requests that have been already replayed.
 *
 * <p>
 *     Add this ContainerRequestFilter to your server, for example
 *     http://blog.dejavu.sk/2013/11/19/registering-resources-and-providers-in-jersey-2/
 *
 *     If you use Guice, simply load DiffyReplayerModule.
 * </p>
 */
@DiffyReplay
@Provider
public class DiffyReplayerFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(DiffyReplayerFilter.class);

    @Context
    private ResourceInfo resourceInfo;
    private final DiffyReplayer diffyReplayer;

    /**
     * Default Constructor.
     */
    public DiffyReplayerFilter() {
        this.diffyReplayer = DiffyReplayer.getInstance();
    }

    /**
     * Constructor to force environment by parameter instead of env var.
     * @param environment
     */
    public DiffyReplayerFilter(String environment) {
        this.diffyReplayer = DiffyReplayer.getInstance(environment);
    }

    /**
     * Constructor only visible for unit tests, so they can inject the diffyreplayer instance.
     *
     * @param resourceInfo Resource Info that will be injected.
     * @param diffyReplayer DiffyReplayer used to replay calls.
     */
    @VisibleForTesting
    DiffyReplayerFilter(ResourceInfo resourceInfo, DiffyReplayer diffyReplayer) {
        this.resourceInfo = resourceInfo;
        this.diffyReplayer = diffyReplayer;
    }

    /**
     * Filters and defines if the request might have the chance to be replayed.
     *
     * Since we are using DiffyReplay annotation, only requests that are annotated with the annotation will get here.
     *
     * @param original original request
     */
    @Override
    public void filter(ContainerRequestContext original) {
        Preconditions.checkNotNull(original);
        Preconditions.checkNotNull(resourceInfo);
        // We add that header to the original request and check here so we don't end up
        // doing an infinite loop.
        if (!original.getHeaders().containsKey(DiffyReplay.HEADER)) {
            // We only allow GETS, since we dont want to replay POSTS or PUTS, etc.
            if ("GET".equals(original.getMethod())) {
                try {
                    Class<?> diffyClass = resourceInfo.getResourceClass();
                    DiffyReplay diffyReplayClass = diffyClass.getAnnotation(DiffyReplay.class);
                    if (diffyReplayClass != null) {
                        diffyReplayer.replay(original, diffyReplayClass.condition().newInstance());
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    LOG.warn("Failed to instantiate the condition", e);
                }
            }
        }
    }
}
