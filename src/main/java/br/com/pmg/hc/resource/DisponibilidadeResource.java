package br.com.pmg.hc.resource;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import br.com.pmg.hc.dto.DisponibilidadeRequest;
import br.com.pmg.hc.dto.DisponibilidadeResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.service.DisponibilidadeService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/disponibilidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DisponibilidadeResource {

    @Inject
    DisponibilidadeService disponibilidadeService;

    @GET
    public List<DisponibilidadeResponse> listar(@QueryParam("profissionalId") Long profissionalId,
            @QueryParam("dataInicial") LocalDate dataInicial,
            @QueryParam("dataFinal") LocalDate dataFinal) {
        if (profissionalId == null) {
            throw new BusinessException("Informe o profissionalId para listar disponibilidades");
        }
        return disponibilidadeService.listarLivres(profissionalId, dataInicial, dataFinal);
    }

    @POST
    public Response criar(@Valid DisponibilidadeRequest request) {
        var response = disponibilidadeService.criar(request);
        return Response.created(URI.create("/disponibilidades/" + response.id())).entity(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        disponibilidadeService.remover(id);
        return Response.noContent().build();
    }
}
