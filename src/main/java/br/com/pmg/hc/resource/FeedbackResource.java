package br.com.pmg.hc.resource;

import java.net.URI;
import java.util.List;

import br.com.pmg.hc.dto.FeedbackRequest;
import br.com.pmg.hc.dto.FeedbackResponse;
import br.com.pmg.hc.model.Usuario;
import br.com.pmg.hc.service.FeedbackService;
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

@Path("/feedbacks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    @Inject
    FeedbackService feedbackService;

    @Inject
    UsuarioService usuarioService;

    @POST
    public Response criar(@HeaderParam("X-Usuario-Id") Long usuarioId, @Valid FeedbackRequest request) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        var response = feedbackService.criar(solicitante, request);
        return Response.created(URI.create("/feedbacks/" + response.id())).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public FeedbackResponse atualizar(@HeaderParam("X-Usuario-Id") Long usuarioId,
            @PathParam("id") Long id,
            @Valid FeedbackRequest request) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return feedbackService.atualizar(id, solicitante, request);
    }

    @GET
    public List<FeedbackResponse> listar(@HeaderParam("X-Usuario-Id") Long usuarioId) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return feedbackService.listar(solicitante);
    }

    @GET
    @Path("/{id}")
    public FeedbackResponse buscarPorId(@HeaderParam("X-Usuario-Id") Long usuarioId, @PathParam("id") Long id) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        return feedbackService.buscarPorId(id, solicitante);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@HeaderParam("X-Usuario-Id") Long usuarioId, @PathParam("id") Long id) {
        Usuario solicitante = usuarioService.recuperarUsuarioAutenticado(usuarioId);
        feedbackService.remover(id, solicitante);
        return Response.noContent().build();
    }
}
