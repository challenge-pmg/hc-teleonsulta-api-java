package br.com.pmg.hc.resource;

import java.net.URI;
import java.util.List;

import br.com.pmg.hc.dto.ConsultaRequest;
import br.com.pmg.hc.dto.ConsultaResponse;
import br.com.pmg.hc.dto.ConsultaStatusRequest;
import br.com.pmg.hc.service.ConsultaService;
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

@Path("/consultas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConsultaResource {

    @Inject
    ConsultaService consultaService;

    @GET
    public List<ConsultaResponse> listar() {
        return consultaService.listarTodas();
    }

    @GET
    @Path("/{id}")
    public ConsultaResponse buscar(@PathParam("id") Long id) {
        return consultaService.buscarPorId(id);
    }

    @POST
    public Response criar(@Valid ConsultaRequest request) {
        var response = consultaService.criar(request);
        return Response.created(URI.create("/consultas/" + response.id())).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public ConsultaResponse atualizar(@PathParam("id") Long id, @Valid ConsultaRequest request) {
        return consultaService.atualizar(id, request);
    }

    @PUT
    @Path("/{id}/status")
    public ConsultaResponse atualizarStatus(@PathParam("id") Long id, @Valid ConsultaStatusRequest request) {
        return consultaService.atualizarStatus(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        consultaService.remover(id);
        return Response.noContent().build();
    }
}
