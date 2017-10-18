package io.split.diffyreplayer.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.ws.rs.container.ContainerRequestContext;

public class ContainerRequestUtil {

    private static String path(ContainerRequestContext original) {
        Preconditions.checkNotNull(original);
        return original.getUriInfo().getRequestUri().getRawPath();
    }

    private static String query(ContainerRequestContext original) {
        Preconditions.checkNotNull(original);
        return original.getUriInfo().getRequestUri().getQuery();
    }

    public static String getPathWithQueryParams(ContainerRequestContext original) {
        String path = path(original);
        String query = query(original);
        return Strings.isNullOrEmpty(query) ? path : path + "?" + query;
    }
}
