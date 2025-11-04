package br.com.pmg.hc.resource;

import java.net.URI;
import java.util.List;

import br.com.pmg.hc.dto.PacienteRequest;
import br.com.pmg.hc.dto.PacienteResponse;
import br.com.pmg.hc.model.Usuario;
import br.com.pmg.hc.service.PacienteService;
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

@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteResource {

    @Inject
    PacienteService pacienteService;

    @Inject
    UsuarioService usuarioService;

    @POST
    public Response criar(@HeaderParam("X-Usuario-Id") Long usuarioId, @Valid PacienteRequest request) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        var response = pacienteService.criar(solicitante, request);
        return Response.created(URI.create("/pacientes/" + response.id())).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public PacienteResponse atualizar(@HeaderParam("X-Usuario-Id") Long usuarioId,
            @PathParam("id") Long id,
            @Valid PacienteRequest request) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return pacienteService.atualizar(id, solicitante, request);
    }

    @GET
    public List<PacienteResponse> listar(@HeaderParam("X-Usuario-Id") Long usuarioId) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return pacienteService.listarTodos(solicitante);
    }

    @GET
    @Path("/{id}")
    public PacienteResponse buscarPorId(@HeaderParam("X-Usuario-Id") Long usuarioId, @PathParam("id") Long id) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return pacienteService.buscarPorId(id, solicitante);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@HeaderParam("X-Usuario-Id") Long usuarioId, @PathParam("id") Long id) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        pacienteService.remover(id, solicitante);
        return Response.noContent().build();
    }
}
