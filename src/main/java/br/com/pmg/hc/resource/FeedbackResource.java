package br.com.pmg.hc.resource;

import java.net.URI;
import java.util.List;

import br.com.pmg.hc.dto.FeedbackRequest;
import br.com.pmg.hc.dto.FeedbackResponse;
import br.com.pmg.hc.service.FeedbackService;
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

@Path("/feedbacks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    @Inject
    FeedbackService feedbackService;

    @POST
    public Response criar(@Valid FeedbackRequest request) {
        var response = feedbackService.criar(request);
        return Response.created(URI.create("/feedbacks/" + response.id())).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public FeedbackResponse atualizar(@PathParam("id") Long id, @Valid FeedbackRequest request) {
        return feedbackService.atualizar(id, request);
    }

    @GET
    public List<FeedbackResponse> listar() {
        return feedbackService.listarTodos();
    }

    @GET
    @Path("/{id}")
    public FeedbackResponse buscarPorId(@PathParam("id") Long id) {
        return feedbackService.buscarPorId(id);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        feedbackService.remover(id);
        return Response.noContent().build();
    }
}
