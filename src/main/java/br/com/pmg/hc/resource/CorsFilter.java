package br.com.pmg.hc.resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
@ApplicationScoped
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String HEADER_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String HEADER_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String HEADER_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String HEADER_MAX_AGE = "Access-Control-Max-Age";
    private static final String HEADER_VARY = "Vary";

    @ConfigProperty(name = "app.cors.allowed-origins")
    String allowedOriginsProperty;

    private Set<String> allowedOrigins;

    @PostConstruct
    void init() {
        allowedOrigins = Arrays.stream(allowedOriginsProperty.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            String origin = requestContext.getHeaderString("Origin");
            if (isAllowed(origin)) {
                Response.ResponseBuilder builder = Response.noContent();
                builder.header(HEADER_ALLOW_ORIGIN, origin);
                builder.header(HEADER_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS");
                builder.header(HEADER_ALLOW_HEADERS, "Content-Type,Authorization,X-User-Id,X-User-Role");
                builder.header(HEADER_MAX_AGE, "86400");
                builder.header(HEADER_VARY, "Origin");
                requestContext.abortWith(builder.build());
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        String origin = requestContext.getHeaderString("Origin");
        if (isAllowed(origin)) {
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            headers.putSingle(HEADER_ALLOW_ORIGIN, origin);
            headers.putSingle(HEADER_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS");
            headers.putSingle(HEADER_ALLOW_HEADERS, "Content-Type,Authorization,X-User-Id,X-User-Role");
            headers.putSingle(HEADER_MAX_AGE, "86400");
            headers.putSingle(HEADER_VARY, "Origin");
        }
    }

    private boolean isAllowed(String origin) {
        return origin != null && allowedOrigins.contains(origin);
    }
}
