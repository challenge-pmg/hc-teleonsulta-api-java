package br.com.pmg.hc.resource;

import java.net.URI;
import java.util.List;

import br.com.pmg.hc.dto.ProfissionalRequest;
import br.com.pmg.hc.dto.ProfissionalResponse;
import br.com.pmg.hc.model.TipoProfissionalSaude;
import br.com.pmg.hc.service.ProfissionalService;
import br.com.pmg.hc.service.TipoProfissionalSaudeService;
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

@Path("/profissionais")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProfissionalResource {

    @Inject
    ProfissionalService profissionalService;

    @Inject
    TipoProfissionalSaudeService tipoProfissionalSaudeService;

    @GET
    public List<ProfissionalResponse> listar() {
        return profissionalService.listarTodos();
    }

    @GET
    @Path("/{id}")
    public ProfissionalResponse buscar(@PathParam("id") Long id) {
        return profissionalService.buscarPorId(id);
    }

    @POST
    public Response criar(@Valid ProfissionalRequest request) {
        var response = profissionalService.criar(request);
        return Response.created(URI.create("/profissionais/" + response.id())).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public ProfissionalResponse atualizar(@PathParam("id") Long id, @Valid ProfissionalRequest request) {
        return profissionalService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        profissionalService.remover(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/tipos")
    public List<TipoProfissionalSaude> listarTipos() {
        return tipoProfissionalSaudeService.listarTodos();
    }
}
