package br.com.pmg.hc.resource;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/warmup")
@Produces(MediaType.APPLICATION_JSON)
public class WarmupResource {

    private static final Logger LOGGER = Logger.getLogger(WarmupResource.class);

    @Inject
    DataSource dataSource;

    @GET
    public Response warmup() {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("select 1 from dual")) {
            statement.execute();
            return Response.noContent().build();
        } catch (Exception e) {
            LOGGER.warn("Warmup failed to reach database", e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new SimpleWarmupResponse("Database not reachable: " + e.getMessage()))
                    .build();
        }
    }

    public record SimpleWarmupResponse(String message) {
    }
}
