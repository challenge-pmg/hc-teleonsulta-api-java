package br.com.pmg.hc.resource;

import java.net.URI;
import java.util.List;

import br.com.pmg.hc.dto.UsuarioRequest;
import br.com.pmg.hc.dto.UsuarioResponse;
import br.com.pmg.hc.service.UsuarioService;
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

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @GET
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GET
    @Path("/{id}")
    public UsuarioResponse buscar(@PathParam("id") Long id) {
        return usuarioService.buscarPorId(id);
    }

    @POST
    public Response criar(@Valid UsuarioRequest request) {
        var response = usuarioService.criar(request);
        return Response.created(URI.create("/usuarios/" + response.id())).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public UsuarioResponse atualizar(@PathParam("id") Long id, @Valid UsuarioRequest request) {
        return usuarioService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        usuarioService.remover(id);
        return Response.noContent().build();
    }
}
