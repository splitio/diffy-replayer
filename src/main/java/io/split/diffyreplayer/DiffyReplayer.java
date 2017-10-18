package io.split.diffyreplayer;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.split.diffyreplayer.condition.DiffyReplayerCondition;
import io.split.diffyreplayer.util.ContainerRequestUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * In charge of asynchronously checking if the conditions are met to replay a query.
 */
public class DiffyReplayer implements AutoCloseable {
    // Simple Singleton Instance. Since it is only an executor that will sit idle
    // it's not worth doing some other type of lazy initialization singleton.
    // Since this will be open sourced, cannot use Guice Singleton
    // Cannot use enum Singleton since I cannot mock an enum for unit tests.
    public static final DiffyReplayer INSTANCE = new DiffyReplayer();

    private static final Logger LOG = LoggerFactory.getLogger(DiffyReplayer.class);

    private final ExecutorService executor;
    private final String diffyUrl;

    /**
     * Should never be called. Use the Singleton INSTANCE.
     */
    private DiffyReplayer() {
        this.executor = Executors.newFixedThreadPool(DiffyReplayerProperties.INSTANCE.getDiffyThreadPool());
        this.diffyUrl = DiffyReplayerProperties.INSTANCE.getDiffyUrl();
        ((ThreadPoolExecutor) executor).setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * Evaluates if a request will be replayed, and if so clones the request and sends it to
     * Diffy.
     *
     * @param original the original request that might be replayed.
     * @param condition DiffCondition that decides whether to replay or not replay a diffy request.
     */
    public void replay(ContainerRequestContext original, DiffyReplayerCondition condition) {
        Preconditions.checkNotNull(original);

        executor.submit(() -> {
            if (condition.replay() && !Strings.isNullOrEmpty(diffyUrl)) {
                String pathWithQueryParams = ContainerRequestUtil.getPathWithQueryParams(original);
                LOG.info(String.format("Replaying request %s to url %s", pathWithQueryParams, diffyUrl));
                DiffyReplayerRequestBuilder builder = new DiffyReplayerRequestBuilder(diffyUrl);
                HttpRequestBase request = builder.build(original);
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    CloseableHttpResponse response = client.execute(request);
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != Response.Status.OK.getStatusCode()) {
                        // Just in Case for some reason getURI is null, don't think it can happen.
                        if (request.getURI() != null) {
                            LOG.warn(
                                    String.format("Failed to execute request %s, status code %s, reason %s",
                                            request.getURI().toString(),
                                            statusCode,
                                            response.getStatusLine().getReasonPhrase()));
                        }
                    }
                } catch (IOException e) {
                    // Just in Case for some reason getURI is null, don't think it can happen.
                    if (request.getURI() != null) {
                        // Only printing the exception message since if there is a problem do not
                        // want to clutter the logs.
                        LOG.warn(String.format("Failed to execute request %s, reason %s, is Diffy Server up and running?",
                                request.getURI().toString(), e.getMessage()));
                    }
                }
            }
        });
    }

    @Override
    public void close() throws Exception {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
