package br.com.pmg.hc.resource;

import java.net.URI;
import java.util.List;

import br.com.pmg.hc.dto.PacienteRequest;
import br.com.pmg.hc.dto.PacienteResponse;
import br.com.pmg.hc.service.PacienteService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteResource {

    @Inject
    PacienteService pacienteService;

    @GET
    public List<PacienteResponse> listar() {
        return pacienteService.listarTodos();
    }

    @GET
    @Path("/{id}")
    public PacienteResponse buscar(@PathParam("id") Long id) {
        return pacienteService.buscarPorId(id);
    }

    @POST
    public Response criar(@Valid PacienteRequest request) {
        var response = pacienteService.criar(request);
        return Response.created(URI.create("/pacientes/" + response.id())).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public PacienteResponse atualizar(@PathParam("id") Long id, @Valid PacienteRequest request) {
        return pacienteService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        pacienteService.remover(id);
        return Response.noContent().build();
    }
}
