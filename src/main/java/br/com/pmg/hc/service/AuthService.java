package br.com.pmg.hc.service;

import br.com.pmg.hc.dao.PacienteDAO;
import br.com.pmg.hc.dao.ProfissionalDAO;
import br.com.pmg.hc.dao.UsuarioDAO;
import br.com.pmg.hc.dto.LoginRequest;
import br.com.pmg.hc.dto.LoginResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    PacienteDAO pacienteDAO;

    @Inject
    ProfissionalDAO profissionalDAO;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioDAO.findByEmail(request.email())
                .filter(u -> u.getSenha().equals(request.senha()))
                .orElseThrow(() -> new BusinessException("Credenciais invalidas"));

        Long pacienteId = null;
        Long profissionalId = null;
        if (usuario.getRole() == Role.PACIENTE) {
            pacienteId = pacienteDAO.findByUsuarioId(usuario.getId())
                    .map(p -> p.getId())
                    .orElse(null);
        } else if (usuario.getRole() == Role.PROFISSIONAL) {
            profissionalId = profissionalDAO.findByUsuarioId(usuario.getId())
                    .map(p -> p.getId())
                    .orElse(null);
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                pacienteId,
                profissionalId);
    }
}
