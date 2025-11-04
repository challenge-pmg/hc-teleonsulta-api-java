package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.ProfissionalDAO;
import br.com.pmg.hc.dto.ProfissionalRequest;
import br.com.pmg.hc.dto.ProfissionalResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProfissionalService {

    @Inject
    ProfissionalDAO profissionalDAO;

    @Transactional
    public ProfissionalResponse criar(ProfissionalRequest request) {
        profissionalDAO.findByEmail(request.email()).ifPresent(p -> {
            throw new BusinessException("Já existe um profissional cadastrado com este e-mail");
        });

        var profissional = new Profissional();
        profissional.setNome(request.nome());
        profissional.setEmail(request.email());
        profissional.setSenha(request.senha());
        profissional.setRole(Role.PROFISSIONAL);
        profissional.setEspecialidade(request.especialidade());
        profissional.setRegistroProfissional(request.registroProfissional());
        profissional.setTelefone(request.telefone());

        profissionalDAO.persist(profissional);
        return toResponse(profissional);
    }

    @Transactional
    public ProfissionalResponse atualizar(Long id, ProfissionalRequest request) {
        var profissional = profissionalDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));

        profissionalDAO.findByEmail(request.email()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException("Já existe outro profissional com este e-mail");
            }
        });

        profissional.setNome(request.nome());
        profissional.setEmail(request.email());
        profissional.setSenha(request.senha());
        profissional.setRole(Role.PROFISSIONAL);
        profissional.setEspecialidade(request.especialidade());
        profissional.setRegistroProfissional(request.registroProfissional());
        profissional.setTelefone(request.telefone());

        var atualizado = profissionalDAO.merge(profissional);
        return toResponse(atualizado);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public ProfissionalResponse buscarPorId(Long id) {
        var profissional = profissionalDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));
        return toResponse(profissional);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ProfissionalResponse> listarTodos() {
        return profissionalDAO.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void remover(Long id) {
        var profissional = profissionalDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));
        profissionalDAO.delete(profissional);
    }

    private ProfissionalResponse toResponse(Profissional profissional) {
        return new ProfissionalResponse(
                profissional.getId(),
                profissional.getNome(),
                profissional.getEmail(),
                profissional.getRole(),
                profissional.getEspecialidade(),
                profissional.getRegistroProfissional(),
                profissional.getTelefone(),
                profissional.getCriadoEm(),
                profissional.getAtualizadoEm());
    }
}
