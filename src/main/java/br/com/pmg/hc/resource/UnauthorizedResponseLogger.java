package br.com.pmg.hc.resource;

import java.io.IOException;

import org.jboss.logging.Logger;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
@Priority(Priorities.USER)
public class UnauthorizedResponseLogger implements ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(UnauthorizedResponseLogger.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        if (responseContext.getStatus() == 401) {
            LOGGER.warnf("Resposta 401 para %s %s (origin=%s)",
                    requestContext.getMethod(),
                    requestContext.getUriInfo().getRequestUri().toString(),
                    requestContext.getHeaderString("Origin"));
        }
    }
}
