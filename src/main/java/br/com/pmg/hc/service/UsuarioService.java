package br.com.pmg.hc.service;

import br.com.pmg.hc.dao.UsuarioDAO;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioDAO usuarioDAO;

    public Usuario recuperarUsuarioAutenticado(Long usuarioId) {
        if (usuarioId == null) {
            throw new BusinessException("Cabecalho X-Usuario-Id obrigatorio");
        }
        return usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado nao encontrado"));
    }
}
