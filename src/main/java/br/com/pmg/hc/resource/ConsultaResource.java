package br.com.pmg.hc.resource;

import java.net.URI;
import java.util.List;

import br.com.pmg.hc.dto.ConsultaRequest;
import br.com.pmg.hc.dto.ConsultaResponse;
import br.com.pmg.hc.dto.ConsultaStatusRequest;
import br.com.pmg.hc.model.Usuario;
import br.com.pmg.hc.service.ConsultaService;
import br.com.pmg.hc.service.UsuarioService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
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

    @Inject
    UsuarioService usuarioService;

    @POST
    public Response criar(@HeaderParam("X-Usuario-Id") Long usuarioId, @Valid ConsultaRequest request) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        var response = consultaService.criar(solicitante, request);
        return Response.created(URI.create("/consultas/" + response.id())).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public ConsultaResponse atualizar(@HeaderParam("X-Usuario-Id") Long usuarioId,
            @PathParam("id") Long id,
            @Valid ConsultaRequest request) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return consultaService.atualizar(id, solicitante, request);
    }

    @PUT
    @Path("/{id}/status")
    public ConsultaResponse atualizarStatus(@HeaderParam("X-Usuario-Id") Long usuarioId,
            @PathParam("id") Long id,
            @Valid ConsultaStatusRequest request) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return consultaService.atualizarStatus(id, solicitante, request);
    }

    @GET
    public List<ConsultaResponse> listar(@HeaderParam("X-Usuario-Id") Long usuarioId) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return consultaService.listarTodas(solicitante);
    }

    @GET
    @Path("/{id}")
    public ConsultaResponse buscarPorId(@HeaderParam("X-Usuario-Id") Long usuarioId, @PathParam("id") Long id) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return consultaService.buscarPorId(id, solicitante);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@HeaderParam("X-Usuario-Id") Long usuarioId, @PathParam("id") Long id) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        consultaService.remover(id, solicitante);
        return Response.noContent().build();
    }
}
