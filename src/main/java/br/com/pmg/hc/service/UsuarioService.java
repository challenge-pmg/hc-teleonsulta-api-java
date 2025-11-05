package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.UsuarioDAO;
import br.com.pmg.hc.dto.UsuarioRequest;
import br.com.pmg.hc.dto.UsuarioResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioDAO usuarioDAO;

    public List<UsuarioResponse> listar() {
        return usuarioDAO.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        var usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return toResponse(usuario);
    }

    public UsuarioResponse criar(UsuarioRequest request) {
        usuarioDAO.findByEmail(request.email()).ifPresent(u -> {
            throw new BusinessException("Já existe um usuário com este e-mail");
        });

        var usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setRole(request.role());

        usuario = usuarioDAO.create(usuario);
        return toResponse(usuario);
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        var usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        usuarioDAO.findByEmail(request.email()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException("Já existe um usuário com este e-mail");
            }
        });

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setRole(request.role());

        usuarioDAO.update(usuario);
        return toResponse(usuario);
    }

    public void remover(Long id) {
        usuarioDAO.delete(id);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getCriadoEm());
    }
}
