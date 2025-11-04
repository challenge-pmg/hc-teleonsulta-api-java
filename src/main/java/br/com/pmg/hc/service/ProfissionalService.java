package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.ProfissionalDAO;
import br.com.pmg.hc.dao.TipoProfissionalSaudeDAO;
import br.com.pmg.hc.dao.UsuarioDAO;
import br.com.pmg.hc.dto.ProfissionalRequest;
import br.com.pmg.hc.dto.ProfissionalResponse;
import br.com.pmg.hc.dto.UsuarioResumo;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.TipoProfissionalSaude;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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

    @Transactional
    public ProfissionalResponse criar(Usuario solicitante, ProfissionalRequest request) {
        garantirRole(solicitante, Role.ADMIN);
        validarEmailDisponivel(request.email(), null);
        validarCrmDisponivel(request.crm(), null);

        var tipo = obterTipoProfissional(request.tipoProfissionalId());

        var usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setRole(Role.PROFISSIONAL);
        usuarioDAO.persist(usuario);

        var profissional = new Profissional();
        profissional.setUsuario(usuario);
        profissional.setTipoProfissional(tipo);
        profissional.setCrm(request.crm());
        if (request.status() != null) {
            profissional.setStatus(request.status());
        }

        profissionalDAO.persist(profissional);
        return toResponse(profissional);
    }

    @Transactional
    public ProfissionalResponse atualizar(Long id, Usuario solicitante, ProfissionalRequest request) {
        var profissional = obterProfissional(id);
        garantirRoleProfissionalOuAdmin(solicitante, profissional);

        validarEmailDisponivel(request.email(), profissional.getUsuario().getId());
        validarCrmDisponivel(request.crm(), profissional.getId());

        var tipo = obterTipoProfissional(request.tipoProfissionalId());

        var usuario = profissional.getUsuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        profissional.setTipoProfissional(tipo);
        profissional.setCrm(request.crm());

        if (request.status() != null) {
            garantirRole(solicitante, Role.ADMIN);
            profissional.setStatus(request.status());
        }

        var atualizado = profissionalDAO.merge(profissional);
        usuarioDAO.merge(usuario);
        return toResponse(atualizado);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public ProfissionalResponse buscarPorId(Long id, Usuario solicitante) {
        var profissional = obterProfissional(id);
        garantirRoleProfissionalOuAdmin(solicitante, profissional);
        return toResponse(profissional);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ProfissionalResponse> listarTodos(Usuario solicitante) {
        garantirRole(solicitante, Role.ADMIN);
        return profissionalDAO.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void remover(Long id, Usuario solicitante) {
        garantirRole(solicitante, Role.ADMIN);
        var profissional = obterProfissional(id);

        if (consultaDAO.existsByProfissional(profissional.getId())) {
            throw new BusinessException("Nao e possivel remover o profissional com consultas vinculadas");
        }
        if (consultaDAO.existsByUsuarioAgendador(profissional.getUsuario().getId())) {
            throw new BusinessException("Nao e possivel remover o profissional que agendou consultas");
        }

        profissionalDAO.delete(profissional);
    }

    public List<TipoProfissionalSaude> listarTiposProfissionais() {
        return tipoProfissionalSaudeDAO.findAll();
    }

    private Profissional obterProfissional(Long id) {
        return profissionalDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional nao encontrado"));
    }

    private TipoProfissionalSaude obterTipoProfissional(Long id) {
        return tipoProfissionalSaudeDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Tipo de profissional de saude nao encontrado"));
    }

    private void validarEmailDisponivel(String email, Long usuarioAtualId) {
        usuarioDAO.findByEmail(email).ifPresent(usuarioExistente -> {
            if (usuarioAtualId == null || !usuarioExistente.getId().equals(usuarioAtualId)) {
                throw new BusinessException("Ja existe um usuario com este e-mail");
            }
        });
    }

    private void validarCrmDisponivel(String crm, Long profissionalAtualId) {
        if (crm == null || crm.isBlank()) {
            return;
        }
        profissionalDAO.findByCrm(crm).ifPresent(profissionalExistente -> {
            if (profissionalAtualId == null || !profissionalExistente.getId().equals(profissionalAtualId)) {
                throw new BusinessException("Ja existe um profissional com este CRM");
            }
        });
    }

    private void garantirRole(Usuario usuario, Role... rolesPermitidos) {
        for (var role : rolesPermitidos) {
            if (usuario.getRole() == role) {
                return;
            }
        }
        throw new BusinessException("Usuario sem permissao para esta operacao");
    }

    private void garantirRoleProfissionalOuAdmin(Usuario solicitante, Profissional profissional) {
        if (solicitante.getRole() == Role.ADMIN) {
            return;
        }
        if (solicitante.getRole() == Role.PROFISSIONAL && solicitante.getId().equals(profissional.getUsuario().getId())) {
            return;
        }
        throw new BusinessException("Usuario sem permissao para esta operacao");
    }

    private ProfissionalResponse toResponse(Profissional profissional) {
        var usuario = profissional.getUsuario();
        var usuarioResumo = new UsuarioResumo(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getCriadoEm());

        var tipo = profissional.getTipoProfissional();

        return new ProfissionalResponse(
                profissional.getId(),
                usuarioResumo,
                tipo.getId(),
                tipo.getNome(),
                profissional.getCrm(),
                profissional.getStatus());
    }
}
