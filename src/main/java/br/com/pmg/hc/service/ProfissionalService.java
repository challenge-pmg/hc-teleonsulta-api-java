package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.ProfissionalDAO;
import br.com.pmg.hc.dao.TipoProfissionalSaudeDAO;
import br.com.pmg.hc.dao.UsuarioDAO;
import br.com.pmg.hc.dto.ProfissionalRequest;
import br.com.pmg.hc.dto.ProfissionalResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.StatusCadastro;
import br.com.pmg.hc.model.TipoProfissionalSaude;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProfissionalService {

    @Inject
    ProfissionalDAO profissionalDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    TipoProfissionalSaudeDAO tipoProfissionalSaudeDAO;

    @Inject
    ConsultaDAO consultaDAO;

    public List<ProfissionalResponse> listarTodos() {
        return profissionalDAO.findAll().stream().map(this::toResponse).toList();
    }

    public ProfissionalResponse buscarPorId(Long id) {
        var profissional = profissionalDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional nao encontrado"));
        return toResponse(profissional);
    }

    public ProfissionalResponse criar(ProfissionalRequest request) {
        usuarioDAO.findByEmail(request.email()).ifPresent(u -> {
            throw new BusinessException("Ja existe um usuario com este e-mail");
        });
        if (request.crm() != null && !request.crm().isBlank()) {
            profissionalDAO.findByCrm(request.crm()).ifPresent(p -> {
                throw new BusinessException("Ja existe um profissional com este CRM");
            });
        }

        TipoProfissionalSaude tipo = tipoProfissionalSaudeDAO.findById(request.tipoProfissionalId())
                .orElseThrow(() -> new BusinessException("Tipo de profissional nao encontrado"));

        var usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        var profissional = new Profissional();
        profissional.setUsuario(usuario);
        profissional.setTipoProfissional(tipo);
        profissional.setCrm(request.crm());
        profissional.setStatus(request.status() != null ? request.status() : StatusCadastro.ATIVO);

        profissional = profissionalDAO.create(profissional);
        return toResponse(profissional);
    }

    public ProfissionalResponse atualizar(Long id, ProfissionalRequest request) {
        var profissional = profissionalDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional nao encontrado"));

        usuarioDAO.findByEmail(request.email()).ifPresent(usuario -> {
            if (!usuario.getId().equals(profissional.getUsuario().getId())) {
                throw new BusinessException("Ja existe um usuario com este e-mail");
            }
        });

        if (request.crm() != null && !request.crm().isBlank()) {
            profissionalDAO.findByCrm(request.crm()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new BusinessException("Ja existe um profissional com este CRM");
                }
            });
        }

        TipoProfissionalSaude tipo = tipoProfissionalSaudeDAO.findById(request.tipoProfissionalId())
                .orElseThrow(() -> new BusinessException("Tipo de profissional nao encontrado"));

        var usuario = profissional.getUsuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        profissional.setTipoProfissional(tipo);
        profissional.setCrm(request.crm());
        profissional.setStatus(request.status() != null ? request.status() : StatusCadastro.ATIVO);

        profissional = profissionalDAO.update(profissional);
        return toResponse(profissional);
    }

    public void remover(Long id) {
        profissionalDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional nao encontrado"));

        if (consultaDAO.existsByProfissional(id)) {
            throw new BusinessException("Nao e possivel remover o profissional com consultas vinculadas");
        }

        profissionalDAO.delete(id);
    }

    private ProfissionalResponse toResponse(Profissional profissional) {
        var usuario = profissional.getUsuario();
        var tipo = profissional.getTipoProfissional();
        return new ProfissionalResponse(
                profissional.getId(),
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                tipo.getId(),
                tipo.getNome(),
                profissional.getCrm(),
                profissional.getStatus());
    }
}
